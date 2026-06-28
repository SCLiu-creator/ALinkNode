const urlBackPic = "http://" + address + ":" + port + "/map/SelfPage/getBackPic";


console.log("main")

let myHead2=document.querySelector("h1");
myHead2.textContent="0"
myHead2.onclick=function(){
    let naa=prompt("w y n");
    alert("hellow"+naa);
};
let st=document.getElementById("show_t");
var str=showTime();
st.textContent=str;

function showTime() {
    var today = new Date;
    var year = today.getFullYear();
    var month = checkNum(today.getMonth() + 1);
    var data = checkNum(today.getDate());
    var hour = today.getHours();
    var minute = checkNum(today.getMinutes());
    var second = checkNum(today.getSeconds());
    var day = today.getDay();
    var a = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    var tip = '上午'
    if (hour > 12) {
        hour -= 12;
        tip = '下午'
    }
    var time = year + '年' + month + '月' + data + '日' + ' ' + tip + ' ' + hour + ':' + minute + ':' +
        second + ' ' + a[day];
    //document.getElementById('time').innerHTML = time;//报错，无法读取内部HTML。
    return time;
}



function checkNum(num) {
    if (num < 10) {
        return '0' + num;
    }
    return num;
}
// const fetchPromise = fetch(
//     // "https://mdn.github.io/learning-area/javascript/apis/fetching-data/can-store/products.json",
//     // "https://zzz.mihoyo.com/?utm_source=pcad360sem001"
//   );
//   console.log(fetchPromise);console.log("已发送请求……");
//   fetchPromise.then((response) => {
//     console.log(`已收到响应：${response.status}`);
//   });

let loadstate=true
let elementload = document.getElementById('loader');
function loadstart() {
    elementload.style.display='flex'
}
function loadstop() {
    elementload.style.display='none'
}
let elementLogout = document.getElementById('Logout');
elementLogout.addEventListener('click',Logout)
async function Logout() {
    await fetch("http://" + address + ":" + port + "/map/over/Logout" , {withCredentials: true})
        .then((Response) => Response.json())
        .catch(e=>{
            window.close();
        }).finally(()=>{
            window.close();
        })
}
"http://" + address + ":" + port + "/map/SelfPage/getBackPic"
document.body.style.backgroundImage = "url(http://" + address + ":" + port + "/map/SelfPage/getBackPic)";
// document.body.style.backgroundImage = "url('your-image-url.jpg')";
// 如果需要设置背景不重复，可以添加以下行
document.body.style.backgroundRepeat = "no-repeat";
// 如果需要设置背景尺寸，可以添加以下行
document.body.style.backgroundSize = "cover"; // 或者 "contain", "100% 100%", 等




let imgbxb=document.querySelector('.imgBx div')

imgbxb.style.backgroundImage = "url(http://" + address + ":" + port + "/map/SelfPage/getUserPic)";
imgbxb.style.backgroundRepeat = "no-repeat";
imgbxb.style.backgroundSize = "cover"; // 或者 "contain", "100% 100%", 等
imgbxb.style.backgroundSize="center"
// setImgBx(imgbxb,"/static/webui/images/008.jpg")
function setImgBx(box,url) {
    fetch("http://" + address + ":" + port + url)
        .then(response => {
            if (!response.ok) {throw new Error("Network response was not ok");}
            return response.blob();
        })
        .then(blo => {
            // 创建一个 URL 对象，用于设置背景图像
            var imageUrl = URL.createObjectURL(blo);
            // 设置元素的背景图像
            box.style.backgroundImage = "url(" + imageUrl + ")";
            box.style.backgroundSize= 'contain';
            box.style.backgroundSize = 'cover';
            box.style.backgroundPosition = 'center';
            // 注意：当图片不再需要时，应该释放 ObjectURL
            // 你可以使用 URL.revokeObjectURL(imageUrl) 方法来释放 URL
        })
        .catch(error => {
            console.error("Error fetching image:", error);
        });
}

let mianpage;
let selectFuns={
    "drag":null
}
let sortFuns={
    "drag":null
}
let selectFun;
function selectSearch(e) {
    let v=document.getElementById('sousuoInput').value
    console.log(v)
    selectFun(v)
}
let sortFunu;
let sortFund;
let sortTime=false;
function sortMuFu(e) {
    let up=document.getElementById('uparrow')
    let down=document.getElementById('downarrow')
    console.log('sort')
    if (e.target===up || up.contains(e.target)){
        if((up.style.backgroundColor==='#bbbbbb' ||  up.style.backgroundColor==="rgb(187, 187, 187)" )&&!sortTime){
            sortTime=true
        }else {
            sortTime=false
        }
        up.style.backgroundColor='#bbbbbb'
        down.style=null
        sortFunu()
        return false
    }
    if (e.target===down || down.contains(e.target)){
        if((down.style.backgroundColor==='#bbbbbb'||  down.style.backgroundColor==="rgb(187, 187, 187)")&&!sortTime){
            sortTime=true
        }else {
            sortTime=false
        }
        down.style.backgroundColor='#bbbbbb'
        up.style=null
        sortFund()
        return false
    }
}
// console.log(el.clientWidth) // 可见区域宽
// console.log(el.clientHeight) // 可见区域高
// console.log(el.offsetWidth) // 可见区域宽 + 边线的宽
// console.log(el.offsetHeight) // 可见区域高 + 边线的宽
// console.log(el.scrollWidth) // 正文全文宽
// console.log(el.scrollHeight) // 正文全文高
// console.log(el.screenTop) // 被卷去的高
// console.log(el.scrollLeft) // 被卷去的左
