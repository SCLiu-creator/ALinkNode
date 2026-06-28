
let create = document.querySelector('.create')
// let Menulist = document.querySelectorAll('.Menulist li')
let userName = ""//list使用
// create.onclick = function (e) {
//     e.stopPropagation();
//     // useName = 1;
//     addLi(userName, 2, 3);
// }


function addLi(_userName, usernick, _usePhone) {
    var li_1 = document.createElement("li");
    userName=_userName;
    li_1.setAttribute("username",_userName)
    // li_1=Menulist.addLi;
    // li_1.li.text="aaaaa";
    //li_1.setAttribute("class", "active");
    let color = '#' + parseInt(Math.random() * 0xFFFFFF).toString(16)
    li_1.setAttribute("style","--bg:"+color)
    // addSpan(li_1, userName);
    let icon = addHelf(li_1);
    li_1.addEventListener('click', activeLink);
    if (usernick!==null && usernick !==undefined && usernick !==""){
        icon.innerText=usernick.charAt(0)
    }else {
        icon.innerText=_userName.charAt(0)
    }

    addtouchdrogCl(icon);
    //  addSpan(li_1,userEamil);
    //  addSpan(li_1,userPhone);
    //addDelBtn(li_1);

    document.querySelector(".Menulist").appendChild(li_1);
    Menulist = document.querySelectorAll('.Menulist li');

    return {"li":li_1, "div":icon};
}
//为姓名或邮箱等添加span标签，好设置样式
function addSpan(li, text) {
    let span_1 = document.createElement("span");
    span_1.innerHTML = text;
    li.appendChild(span_1);
}
function addHelf(li) {
    var helf_1 = document.createElement("a");
    // helf_1.href = "#";
    let icon = addDiv(helf_1);
    li.appendChild(helf_1);
    return icon;
}
function addDiv(he) {
    var div_1 = document.createElement("div");
    var div_2 = document.createElement("div");
    var div_3 = document.createElement("div");
    div_1.setAttribute("class", "icon");
    div_1.addEventListener("click",lodahtml);
    div_1.dataset.username=userName;
    addIcon(div_1);
    addIcon1(div_3);
    div_2.setAttribute("class", "text");

    div_2.textContent = userName;
    div_3.setAttribute("class", "x");
    div_3.addEventListener("click",delLi1);
    he.appendChild(div_1);
    he.appendChild(div_2);
    he.appendChild(div_3);
    return div_1;
}

function addIcon(div) {
    var icon_1 = document.createElement("icon");
    icon_1.setAttribute = ("name", "settings-outline");
    div.appendChild(icon_1);
}
function addIcon1(div) {
    var x = document.createElement("x");
    x.setAttribute = ("name", "settings-outline");
    // x.addEventListener("click",delLi1);
    div.appendChild(x);
}
//添加删除按钮及设置删除按钮的样式及添加点击事件
function addDelBtn(li) {
    var span_1 = document.createElement("span");
    var btn = document.createElement("div");
    btn.setAttribute("type", "div");
    btn.setAttribute("class", "delBtn");
    btn.setAttribute("onclick", "delLi(this)");
    btn.innerHTML = "删除";
    span_1.appendChild(btn);
    li.appendChild(span_1);
}
//为删除按钮添加删除节点功能
function delBtnData(obj) {
    var ul = document.getElementById("Menulist");
    var oLi = obj.parentNode.parentNode;
    //     obj.parentNode指删除按钮的span层
    //    obj.parentNode.parentNode为li层
    ul.removeChild(oLi);
}
function delLi(obj) {
    var ul = document.querySelector(".Menulist");
    var oLi = obj.parentNode.parentNode;
    //     obj.parentNode指删除按钮的span层
    //    obj.parentNode.parentNode为li层
    ul.removeChild(oLi);
}
function delLi1() {
    let Menu = document.querySelector('.Menulist')
    let oLi = this.parentNode.parentNode;
    //     obj.parentNode指删除按钮的span层
    //    obj.parentNode.parentNode为li层
    Menu.removeChild(oLi);
}
