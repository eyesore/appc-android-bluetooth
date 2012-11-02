/**
 * File: tools.js
 * 
 * Utility functions.
 * Some useful for debugging and development.
 * 
 * @author Trey Jones <trey@eyesoreinc.com>
 * @package Eyesore
 * @date 6-18-2012
 */
exports.debug = true;

//*********** DEBUG helpers ******************************
exports.log = function(object)
{
    if(exports.debug)
    {
        var objString = JSON.stringify(object);
        
        if(typeof objString !== 'undefined')
        {
            var itemArray = objString.split(',');
            
            for(item in itemArray)
                Ti.API.info(item + ':  ' + itemArray[item]);
        }
        else
            Ti.API.info(objString);
    }
}

exports.error = function(e)
{
    Ti.API.error('Status: ' + e.status);
    Ti.API.error('Error: ' + e.error);
    Ti.API.error('Reason: ' + e.reason);
};