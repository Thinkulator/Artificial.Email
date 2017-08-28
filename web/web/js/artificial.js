

$(document).ready(function(){
    //Turn times into the local time zone.
    var tzLabel = new Date().toTimeString().match(/\((.+)\)/)[0];
    $('.fixtimelabel').each(function(ix, ele){
        $(ele).append(' '+tzLabel).removeClass("fixtimelabel");
    });    
     
    $('.fixtime').each(function(ix, ele){
        $(ele).text(displayAsLocalTime($(ele).text())).removeClass("fixtime");
    });    
    
    
});

function displayAsLocalTime(timestamp){
    return new Date(Number(timestamp)).toLocaleString();
}