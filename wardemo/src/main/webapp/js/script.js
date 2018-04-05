var canvas = document.getElementById(myCanvas);
var context = canvas.getContext('2d');

context.beginPath();
context.rect(0, 0, 50, 50);
context.fillStyle = "rgb("+ myColor +")";
context.fill();
context.lineWidth = 7;
context.strokeStyle = 'black';
context.stroke();

function myclick(event) {
    var request = new XMLHttpRequest();
    var theUrl = "http://10.0.0.239:8080/demo-1.0-SNAPSHOT/FrameBuffer?i=" + event.target.i + "&j=" + event.target.j + ";
    request.open("GET", theUrl, false);
    request.send(null);
    canvas = document.getElementById(event.target.myCanvas);
    context = canvas.getContext('2d');
    context.fillStyle = "rgb(0, 511, 0 )";
    context.fill();
    context.lineWidth = 7;
    context.strokeStyle = 'black';
    context.stroke();
}
canvas.addEventListener("click", myclick, false);
canvas.myCanvas = myCanvas;
canvas.i = getParam_i;
canvas.j = getParam_j;