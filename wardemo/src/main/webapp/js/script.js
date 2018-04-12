function addCanvas(canvas_id, color, param_i, param_j) {
    var canvas = document.getElementById(canvas_id);
    var context = canvas.getContext('2d');

    context.beginPath();
    context.rect(0, 0, 50, 50);
    context.fillStyle = "rgb("+color +")";
    context.fill();
    context.lineWidth = 7;
    context.strokeStyle = 'black';
    context.stroke();

    function canvasClick(event) {
        canvas = document.getElementById(event.target.canvas_id);
        userColor = document.getElementById("userColor");
        context = canvas.getContext('2d');
        
        var client = new XMLHttpRequest();
        var requestUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname + "?i=" + event.target.i + "&j=" + event.target.j + "&c=0x" + rgbToHex(userColor.value);
        
        client.open("GET", requestUrl);
        client.send();
        
        context.fillStyle = userColor.value;
        context.fill();
        context.lineWidth = 7;
        context.strokeStyle = 'black';
        context.stroke();
    }
    canvas.addEventListener("click", canvasClick, false);
    canvas.canvas_id = canvas_id;
    canvas.i = param_i;
    canvas.j = param_j;
}



function rgbToHex(rgba_str) {
    var colorStr = rgba_str.slice(rgba_str.indexOf('(') + 1, rgba_str.indexOf(')')); // "100, 0, 255, 0.5"
    var colorArr = rgba_str.split(','),
    i = colorArr.length;

    while (i--) {
        colorArr[i] = parseInt(colorArr[i], 10);
    }

    var colorObj = {
        r: colorArr[0],
        g: colorArr[1],
        b: colorArr[2],
        a: colorArr[3]
    }
    
     alert("R:"+colorObj.r+" | G:"+colorObj.g+"| B:"+colorObj.b+" => RGBA: "+rgba_str);
    return toHex(colorObj.r)+toHex(colorObj.g)+toHex(colorObj.b);
}

function toHex(n) {
  n = parseInt(n,10);
  if (isNaN(n)) return "00";
  n = Math.max(0,Math.min(n,255));
  
  return "0123456789ABCDEF".charAt((n-n%16)/16) + "0123456789ABCDEF".charAt(n%16);
}


