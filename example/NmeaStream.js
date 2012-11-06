// File: NmeaStream.js
// Author: Trey Jones <trey@eyesoreinc.com>

/**
 * Definition of NmeaStream type
 * 
 * Stores a configurable amount of Nmea sentences (default 100).
 * Allows fetching of the most recent data.
 * 
 * Allowed Options:
 *  max (100) - Maximum number of lines to cache.
 *  acceptedTypes (undefined) - Array of NMEA sentence identifiers to cache, ignoring others.
 *  cacheExtra (true) - By default, instead of shifting every time we overflow the max, we wait until the stack grows an extra 50%.
 * Disable for slightly less memory usage, and  more CPU.
 */
function NmeaStream(options)
{
    this.max = options.max || 100;
    
    if(options.acceptTypes)
    {
        this.acceptTypes = options.acceptTypes;
        this.typesRestricted = true;
    }
    else 
        this.typesRestricted = false;
    
    this.cacheExtra = options.cacheExtra === false ? false : true;
    
    this._buffer = '';
    this._lines = [];
    this._validSentences = 0;
    this._invalidSentences = 0;
    this._totalSentences = 0;
    
    // TODO _lines could be an object with arrays for each type TYPE, might be more efficient.
    
    // this will be the number of lines to queue over the max before slicing off the front of the array 
    this._maxLineQueue = Math.ceil(this.max * 1.5);
    
    // http://www.tronico.fi/OH6NT/docs/NMEA0183.pdf
    //this._sentenceTemplate = /\$.{1,80}?\\r\\n/;  // currently unused in favor of making assumptions
}

/**
 * @param {String} data
 */
NmeaStream.prototype.addData = function(data)
{
    this._buffer += data;
    this._parse();
};

/**
 * Add a line, then manage the stack.
 * Instead of shifting every time we go over the max, cache extra lines, then slice.
 * 
 * @param {NmeaLine} line
 */
NmeaStream.prototype.addLine = function(line)
{
    if(!this.typesRestricted || this.acceptTypes.indexOf(line.type) !== -1)
        this._lines.push(line);
        
    // manage length of _lines
    if(!this.cacheExtra && this._lines.length > this.max)
        this._lines.shift();
    else if(this._lines.length > this._maxLineQueue)
        this._lines = this._lines.slice(this._lines.length - this.max);
};

// TODO make better
// search through the lines array starting from the end for a line of given type
/**
 * getLast()  Will simply return the last line.
 * 
 * @param {String} type The line sentence Identifier to search for
 * @param {Number} count The maximum number of lines to return.
 * @returns NmeaLine[] if count is greater than 1, else NmeaLine.
 */
NmeaStream.prototype.getLast = function(type, count)
{
    count = count || 1;
    var results = [];
        
    var i;
    for(i = this._lines.length - 1; i >= 0; i--)
    {
        if(!type || this._lines[i].type === type)
            results.push(this._lines[i]);
            
        if(results.length >= count)
            break;
    }
    
    if(results.length === 0)
        return false;
    else if(count == 1)
        return results[0];
    else
        return results;
};

NmeaStream.prototype.getDataQuality = function()
{
    Ti.API.info('Bad Lines: ' + this._invalidSentences);
    Ti.API.info('Total Lines: ' + this._totalSentences);
};

NmeaStream.prototype._parse = function()
{
    // make sure there is a valid sentence in the buffer
    // if(this._buffer.search(this._sentenceTemplate) === -1)
    // {
        // Ti.API.info('No valid sentence found.');
        // Ti.API.info(this._buffer.length);
        // return;
    // }
        
    // assume that first char is always $ and that $ follows each \n
    var lineEnd = this._buffer.indexOf('\n'),
    fullSentence, newLine;
    
    while(lineEnd !== -1)
    {
        fullSentence = this._buffer.slice(0, lineEnd + 1);
        this._buffer = this._buffer.slice(lineEnd + 1);

        this._totalSentences++;
        
        newLine = new NmeaLine(fullSentence);
        if(newLine.isValid)
        {
            this.addLine(newLine);
            this._validSentences++;
        }
        else
            this._invalidSentences++;
        
        lineEnd = this._buffer.indexOf('\n');
    }
};


/**
 * Definition of NmeaLine type.
 * 
 * Extracts data from a NMEA standard sentence.
 * @param {Object} sentence First character is '$', terminates with '\r\n'
 */
function NmeaLine(sentence)
{
    //Ti.API.info(sentence);
    if(!this._validate(sentence))
        this.isValid = false;
    else
        this.isValid = true;
    
    if(this.isValid)    
    {
        this.raw = sentence.trim();  // get rid of termination chars    
        this._init();
    }
    else Ti.API.info(sentence);
}

NmeaLine.prototype._init = function()
{
    this.isProprietary = this.raw[1] == 'P';
    this.isQuery = this.raw[5] == 'Q';
    this.isTalker = (!this.isProprietary && !this.isQuery);
    
    this._setTalker();
    this._setType();
    this._setProperties();  // includes parsing
};

NmeaLine.prototype._setTalker = function()
{    
    if(this.isTalker)
        this.talker = this.raw.slice(1, 3);  // 0 should be $
};

NmeaLine.prototype._setType = function()
{
    if(this.isTalker)
        this.type = this.type = this.raw.slice(3,6);
};

NmeaLine.prototype._setProperties = function()
{
    if(this.isTalker)
    {
        var allValues = this.raw.split(','),
        identifier = allValues.shift();  // should equal this.talker + this.type
        
        // call field getter function with allValues as this
        if(!typeMap[this.type] || typeMap[this.type] == function(){})
        {
            // TODO make better
            // if there isn't a parsing function defined, just use the values
            typeMap[this.type] = function()
            {
                var i, properties = {};
                for(i = 0; i < this.length; i++)
                {
                    properties[this[i]] = this[i];
                }
                
                return properties;
            };
        }
          
        this.properties = typeMap[this.type].call(allValues);
    }
    else if(this.isProprietary)
    {
        Ti.API.info('This is a proprietary sentence');
        Ti.API.info(this.raw);
    }
    else if(this.isQuery)
    {
        Ti.API.info('This is a query sentence.');
        Ti.API.info(this.raw);
    }
};

NmeaLine.prototype._validate = function(sentence)
{
    if(!sentence[0] === '$')
    {
        Ti.API.info('Illegal first character');
        return false;
    }
        
    var fieldsEnd = sentence.indexOf('*');
    
    // don't accept lines without a checksum
    if(fieldsEnd === -1)
    {
        Ti.API.info('No checksum found.');
        return false;
    }
        
    var checkData = sentence.slice(1, fieldsEnd),
    checksum = sentence.substr(fieldsEnd + 1, 2);
    
    // calculate checksum - xor checkdata
    var check = 0, i;
    for(i = 0; i < checkData.length; i++)
    {
        check = check ^ checkData.charCodeAt(i);
    }
    hexCheck = Number(check).toString(16).toUpperCase();
    // pad if less than 2 chars
    if(hexCheck.length < 2)
        hexCheck = ('00' + hexCheck).slice(-2);
        
    if(hexCheck == checksum)
        return true;
    else
    {
        Ti.API.info('Checksum didnt match.');
        return false;
    }    
};

// TODO tj: modules for each of these parsing functions
// function to return fields based on number of values in string
var typeMap = {
    'AAM': function(){},
    'ALM': function(){},
    'APA': function(){},
    'APB': function(){},
    'ASD': function(){},
    'BEC': function(){},
    'BOD': function(){},
    'BWC': function(){},
    'BWR': function(){},
    'BWW': function(){},
    'DBK': function(){},
    'DBS': function(){},
    'DBT': function(){},
    'DCN': function(){},
    'DPT': function(){},
    'DSC': function(){},
    'DSE': function(){},
    'DSI': function(){},
    'DSR': function(){},
    'DTM': function(){},
    'FSI': function(){},
    'GBS': function(){},
    'GGA': function(){},
    'GLC': function(){},
    'GLL': function(){},
    'GRS': function(){},
    'GST': function(){},
    'GSA': function(){},
    'GSV': function(){},
    'GTD': function(){},
    'GXA': function(){},
    'HDG': function(){},
    'HDM': function(){},
    'HDT': function(){},
    'HSC': function(){},
    'LCD': function(){},
    'MSK': function(){},
    'MSS': function(){},
    'MWD': function(){},
    'MTW': function(){},
    'MVW': function(){},
    'OLN': function(){},
    'OSD': function(){},
    'ROO': function(){},
    'RMA': function(){},
    'RMB': function(){},
    'RMC': function()
    {
        var fields = ['time', 'status', 'latitude', 'nors1', 'longitude', 'eorw1',
            'speed', 'track', 'date', 'declination', 'eorw2', 'checksum'],
        properties = {},  
        i;
        for(i = 0; i < fields.length; i++)
        {
            properties[fields[i]] = this[i].trim();
        }
        
        return properties;
    },
    'ROT': function(){},
    'RPM': function(){},
    'RSA': function(){},
    'RSD': function(){},
    'RTE': function(){},
    'SFI': function(){},
    'STN': function(){},
    'TLL': function(){},
    'TRF': function(){},
    'TTM': function(){},
    'VBW': function(){},
    'VDR': function(){},
    'VHW': function(){},
    'VLW': function(){},
    'VPW': function(){},
    'VTG': function(){},
    'VWR': function(){},
    'WCV': function(){},
    'WDC': function(){},
    'WDR': function(){},
    'WNC': function(){},
    'WPL': function(){},
    'XDR': function(){},
    'XTE': function(){},
    'XTR': function(){},
    'ZDA': function(){},
    'ZDL': function(){},
    'ZFO': function(){},
    'ZTG': function(){}    
};

module.exports = NmeaStream;