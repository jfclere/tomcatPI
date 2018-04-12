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



function rgbToHex(RGB) {
    var R = parseInt(RGB.substring(0,2) ,16);
    var G = parseInt(RGB.substring(2,4) ,16);
    var B = parseInt(RGB.substring(4,6) ,16);
     
    return toHex(R)+toHex(G)+toHex(B);
}

function toHex(n) {
  n = parseInt(n,10);
  if (isNaN(n)) return "00";
  n = Math.max(0,Math.min(n,255));return "0123456789ABCDEF".charAt((n-n%16)/16) + "0123456789ABCDEF".charAt(n%16);
}

