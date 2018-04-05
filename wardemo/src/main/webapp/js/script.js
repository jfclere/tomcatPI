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
        var client = new XMLHttpRequest();
        var requestUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname;
        var requestQuery = "i=" + event.target.i + "&j=" + event.target.j;
        
        client.open("GET", requestUrl + "?" + requestQuery);
        client.send();
        
        canvas = document.getElementById(event.target.canvas);
        context = canvas.getContext('2d');
        context.fillStyle = "rgb(0, 511, 0 )";
        context.fill();
        context.lineWidth = 7;
        context.strokeStyle = 'black';
        context.stroke();
        location.reload();
    }
    canvas.addEventListener("click", canvasClick, false);
    canvas.canvas = canvas;
    canvas.i = param_i;
    canvas.j = param_j;
}
