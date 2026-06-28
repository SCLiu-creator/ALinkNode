let canvas = document.getElementById('canvas');
let ctx = canvas.getContext('2d');
let drawing = false;
let startPoint = { x: 0, y: 0 };
let endPoint = { x: 0, y: 0 };
let currentIcon;
canvas.addEventListener('mousedown', (event) => {
    drawing = true;
    startPoint.x = event.x;
    startPoint.y = event.y;
});
canvas.addEventListener('mousemove', (event) => {
    if (!drawing) return;
    endPoint.x = event.x;
    endPoint.y = event.y;
    drawLine();
    checkIcon();
});
canvas.addEventListener('mouseup', () => {
    drawing = false;
});
function drawLine() {
    ctx.beginPath();
    ctx.moveTo(startPoint.x, startPoint.y);
    ctx.lineTo(endPoint.x, endPoint.y);
    ctx.stroke();
}
function checkIcon() {
    // 获取当前图标的位置
    let iconRect = document.getElementById(currentIcon).getBoundingClientRect();
    let iconX = iconRect.left + iconRect.width / 2;
    let iconY = iconRect.top + iconRect.height / 2;
  
    // 检查鼠标位置是否在图标范围内
    if (endPoint.x > iconX - iconRect.width / 2 && 
        endPoint.x < iconX + iconRect.width / 2 && 
        endPoint.y > iconY - iconRect.height / 2 && 
        endPoint.y < iconY + iconRect.height / 2) {
        // 鼠标在图标范围内，执行相关操作，比如更新图标状态等。
        console.log("鼠标经过了图标:", currentIcon);
    } else {
        // 鼠标不在图标范围内，可以清空操作或进行其他处理。
        console.log("鼠标没有经过图标");
    }
}