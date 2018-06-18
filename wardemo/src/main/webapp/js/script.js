$(function () {
    $("#color_picker").colorpicker({
        format: 'rgb',
        inline: false,
        container: true,
        useAlpha: false,
        extensions: [{
            name: 'swatches',
            colors: {
                    'tetrad1': 'rgb(255, 0, 6)',
                    'tetrad2': 'rgb(27, 255, 0)',
                    'tetrad3': 'rgb(225, 255, 0)',
                    'tetrad4': 'rgb(255, 116, 0)',
                    'tetrad5': 'rgb(0, 1, 255)',
                    'tetrad6': 'rgb(228, 0, 255)',
                    'tetrad7': 'rgb(0, 255, 19)',
                    'tetrad8': 'rgb(255, 255, 255)',
                    'tetrad9': 'rgb(0, 0, 0)'
            },
            namesAsValues: false
        }]
    }).on('colorpickerCreate', function (e) {
        e.color.tetrad().forEach(function (color, i) {
            var colorStr = color.toString(e.color.format);
            e.colorpicker.picker.find('.colorpicker-swatch[data-name="tetrad' + (i + 1) + '"]').css('background-color', colorStr).attr('data-value', colorStr).attr('title', colorStr);
        });
    });
});

function changeColor(canvas_id) {
    var canvas = document.getElementById(canvas_id);
    var context = canvas.getContext('2d');
    context.beginPath();
    context.rect(0, 0, 50, 50);
    context.fillStyle = canvas.getAttribute('data-color');
    context.fill();
    context.lineWidth = 7;
    context.strokeStyle = '';
    context.stroke();
}

function initCanvas() {
    for(var i = 0; i < 8; i++) {
         for(var j = 0; j < 8; j++) {
            var canvas = document.getElementById("myCanvas" + i + "X" + j);
            var context = canvas.getContext('2d');
            addCanvas("myCanvas" + i + "X" + j, canvas.getAttribute('data-color'), i, j);
         }
    }
}

function addCanvas(canvas_id, color, param_i, param_j) {
    var canvas = document.getElementById(canvas_id);
    var context = canvas.getContext('2d');
    
    context.beginPath();
    context.rect(0, 0, 50, 50);
    context.fillStyle = color;
    context.fill();
    context.lineWidth = 7;
    context.strokeStyle = '';
    context.stroke();

    function canvasClick(event) {
        canvas = document.getElementById(event.target.canvas_id);
        userColor = document.getElementById("userColor");
        context = canvas.getContext('2d');
        canvas.setAttribute('data-color', userColor.value);

        var client = new XMLHttpRequest();
        var requestUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname;
        var requestData = "i=" + event.target.i + "&j=" + event.target.j + "&red=" + getRed(userColor.value) + "&green=" + getGreen(userColor.value) + "&blue=" + getBlue(userColor.value);
        
        client.open("POST", requestUrl, true);
        client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        client.send(requestData);
        
        changeColor(event.target.canvas_id, userColor.value);
    }
    canvas.addEventListener("click", canvasClick, false);
    canvas.addEventListener("touchstart", canvasClick, false);
    canvas.canvas_id = canvas_id;
    canvas.i = param_i;
    canvas.j = param_j;
}


function getRed(rgb_str) {
  var red = 0;
  var rgb_red = /rgba?\(\s*(\d+),(?:[\d\s]*),(?:[\d\s]*)(?:,\s*[\d\.]+)?\)/.exec(rgb_str);
  if(rgb_red[1]) {
     red = rgb_red[1];
  }

  return red;
}

function getGreen(rgb_str) {
  var green = 0;
  var rgb_green = /rgba?\((?:[\d\s]*),\s*(\d+),(?:[\d\s]*)(?:,\s*[\d\.]+)?\)/.exec(rgb_str);
  if(rgb_green[1]) {
     green = rgb_green[1];
  }

  return green;
}

function getBlue(rgb_str) {
  var blue = 0;
  var rgb_blue = /rgba?\((?:[\d\s]*),(?:[\d\s]*),\s*(\d+)(?:,\s*[\d\.]+)?\)/.exec(rgb_str);
  if(rgb_blue[1]) {
     blue = rgb_blue[1];
  }

  return blue;
}

function RGBtoHEX(rgb_str) {
    var rgb = /rgba?\((\d+), (\d+), (\d+)(?:,\s*[\d\.]+)?\)/.exec(rgb_str);
    var r = rgb[1], g = rgb[2], b = rgb[3];

    return toHex(r)+toHex(g)+toHex(b);
}

function RGBtoRGB565(rgb_str) {
    var rgb = /rgba?\((\d+), (\d+), (\d+)(?:,\s*[\d\.]+)?\)/.exec(rgb_str);
    var r = rgb[1], g = rgb[2], b = rgb[3];
    var rgb565 = (((r & 248)<<8)+((g & 252)<<3)+((b & 248)>>3)).toString(16).toUpperCase();
		while (rgb565.length<4)
			rgb565 = "0" + rgb565;
    return rgb565;
}

function toHex(n) {
  n = parseInt(n,10);
  if (isNaN(n)) return "00";
  n = Math.max(0,Math.min(n,255));
  
  return "0123456789ABCDEF".charAt((n-n%16)/16) + "0123456789ABCDEF".charAt(n%16);
}


function HEXtoRGB(hex) {
	var bigint = parseInt(hex, 16);
	var r = (bigint >> 16) & 255;
	var g = (bigint >> 8) & 255;
	var b = bigint & 255;

	return r+", "+g+", "+b;
}
