let menuToggle = document.querySelector('.menuToggle')
let sidebar = document.querySelector('.sidebar')
let alogo = document.querySelector('#J_List .logo')
let JList = document.getElementById('J_List')
let showuserlist = document.querySelector("#showuserlist");
let logotext = document.querySelector("#J_List .logo .text");

let J_Listoverflow = true;
alogo.onclick = function () {
    console.log("ttttt")

    if (J_Listoverflow) {
        JList.setAttribute('style', 'overflow-x: auto ');
        J_Listoverflow = false;
        showuserlist.setAttribute('style', ' letter-spacing: 6px; ')
    } else {
        JList.setAttribute('style', 'overflow-x: hidden ');
        J_Listoverflow = true;
        showuserlist.setAttribute('style', ' letter-spacing: 60px; ')
        logotext.setAttribute('style', ' letter-spacing: 60px; ')
    }

    // JList.style.overflow-x =''
    menuToggle.classList.toggle('active')
    sidebar.classList.toggle('active')
    // showuserlist.classList.toggle('active')
}

let Menulist = document.querySelectorAll('.Menulist li')
let selectUserDrag;
function activeLink(e) {
    // let b=false
    // for (let menu of Menulist){
    //     if(menu===e.target){
    //         b=true
    //     }
    // }
    //
    // if (!b){
    //     return
    // }
    Menulist.forEach((item) => {
        item.classList.remove('active')
    })
    this.classList.add('active')
}
function hideLink(e) {
    const u=this.getAttribute('username')
    if (u===null||u===undefined){
        // lodahtml(false)
        // scrollToEle(document.querySelector('.rightbody'),navPara)
    }
}

Menulist.forEach((item) => {
    item.addEventListener('click', activeLink)
    item.addEventListener('click', hideLink)
})
let localUser
fetch("http://" + address + ":" + port + "/map/Index/getUser").then(Response => Response.json())
    .then(json => {
        let name = document.querySelector("#showuserlist");
        name.dataset = json
        name.innerText = json['nickName']
        let user = document.querySelector(".logo a");
        user.innerHTML = '<div class="text" style="margin-left: 37px">' + '      ' + json['username'] + '</div>'
        localUser = json['username']
        }
    )
// <img alt="" src="https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2017/11/27/15ffde661029595b~tplv-t2oaga2asx-jj-mark:3024:0:0:0:q75.png"
// loading="lazy" class="medium-zoom-image">


let dragloaddisplaystate = true
let elementuser = document.querySelector('.imgBx');
elementuser.addEventListener('click', loaduser)

// loaduser()
function loaduser(e) {
    //!element0 && typeof(element0)!="undefined" && element0 !== 0
    console.log("dragloaddisplaystate: " + dragloaddisplaystate)
    if (elementdrag.childElementCount > 0) {
        if (dragloaddisplaystate) {
            dragload(0);
            // elementlist.style.display = 'none'

            elementdrag.style.display = 'flex'

            let targetElement = document.getElementById('dl');
            containerWheel.scrollLeft = targetElement.offsetLeft-78 +navPara;
            // containerWheel.scrollLeft = targetElement.offsetLeft -navPara;
        } else {
            // dragload();
            // elementdrag.style.display = 'none'
            elementlist.style.display = 'flex'
            let targetElement = document.getElementById('content-ll');
            containerWheel.scrollLeft = targetElement.offsetLeft-79 +navPara;
            // containerWheel.scrollLeft = targetElement.offsetLeft-2 -navPara;
        }
        dragloaddisplaystate = !dragloaddisplaystate
    } else {
        $('#drag').load('htmls/cloudeFile.html' + '#' + window.username);
        // scrollToEle(container)
        // $('#table1').load('htmls/table1.html');
        // dragload();
    }

    // element0.innerHTML='<div id="${this.dataset.username}"></div>'

}

// let dragloaddisplaystate=true
let elementdrag = document.getElementById('drag');
let elementlist = document.getElementById('list');
function lodahtml(state) {
    //!element0 && typeof(element0)!="undefined" && element0 !== 0
    console.log("dragloaddisplaystate: "+dragloaddisplaystate)
    if (state!==null && state!==undefined && typeof(state) ==="boolean"){
        dragloaddisplaystate=state

    }else {
        if (window.username!==this.dataset.username){
            window.username=this.dataset.username;
            dragloaddisplaystate=true
        }
    }

    if (elementdrag.childElementCount > 0) {

        if (dragloaddisplaystate) {
            dragload();
            // elementlist.style.display='none'
            elementdrag.style.display = 'flex'
            dragloaddisplaystate=false
        } else {
            // dragload();
            elementdrag.style.display = 'none'
            elementlist.style.display='flex'
            dragloaddisplaystate=true
        }
        // dragloaddisplaystate = !dragloaddisplaystate
    } else {
        $('#drag').load('htmls/cloudeFile.html' + '#' + window.username);
        if (dragloaddisplaystate) {
            // elementlist.style.display='none'
            elementdrag.style.display = 'flex'
            dragloaddisplaystate=false
        } else {
            // elementdrag.style.display = 'none'
            elementlist.style.display='flex'
            dragloaddisplaystate=true
        }
        // dragloaddisplaystate = !dragloaddisplaystate
        // $('#table1').load('htmls/table1.html');
        // dragload();
    }
    // element0.innerHTML='<div id="${this.dataset.username}"></div>'
}


let rightCon = document.querySelector('.right-content')
let leftbody = document.querySelector('.leftbody')
let NavConInterface = document.querySelector('#navBarI')
let NavConIfState = false;
let leftbodystate = true;
let menuToggleState = false;
// menuToggle.onclick =
let menuToggleTime;
let menuToggleSetTime;
menuToggle.addEventListener('mousedown', menuToggleDown)
menuToggle.addEventListener('mouseup', menuToggleUp)
menuToggle.addEventListener('mouseout', menuToggleUp)

menuToggle.addEventListener('touchstart', menuToggleDown)
menuToggle.addEventListener('touchmove', menuToggleMove)
menuToggle.addEventListener('touchmove', menuToggleUp)
menuToggle.addEventListener('touchend', menuToggleUp)
let menuToggleX=null
let menuToggleY=null
let deltaMenuToggleX;
let deltaMenuToggleY;
function menuToggleDown(e) {
    if (e.target !== this) {
        return
    }
    menuToggleState =true
    // menuToggle.style.transform='scale(1.25)'
    menuToggle.style.borderRadius = '30px'

    menuToggleTime = new Date().getTime()
    menuToggleSetTime = setTimeout(() => {
        console.log("长按事件");
        if (NavConIfState) {
            document.querySelector("#navBG").style.display = 'none'
            document.querySelector("#navCon").style.display = 'none'

            document.querySelector("#polyViewBI").style.display = 'none'
            // NavConInterface.style.display='none'
            NavConIfState = false
        } else {
            document.querySelector("#navBG").style.display = 'flex'
            document.querySelector("#navBG").style.zIndex = '7'
            document.querySelector("#navCon").style.display = 'flex'
            document.querySelector("#navBCon").style.display = 'flex'

            document.querySelector("#polyViewBI").style.display = 'flex'
            // NavConInterface.style.display='flex'
            NavConIfState = true
        }
    }, 600)
    if (menuToggleX==null){
        menuToggleX=e.touches[0].clientX
    }
    if (menuToggleY==null){
        menuToggleY=e.touches[0].clientY
    }
    // if (leftbodystate){
    //     leftbody.style.display='none'
    //     // leftbody.style.width='8px'
    //     rightCon.style.marginLeft='2px'
    //     leftbodystate=false;
    // }else {
    //     leftbody.style.display=''
    //     // leftbody.style.width='8px'
    //     rightCon.style.marginLeft='78px'
    //     leftbodystate=true;
    // }
    // // menuToggle.style.display='flex'
    // menuToggle.classList.toggle('active')
    // // sidebar.classList.toggle('active')
}

leftbody.style.display = 'none'
// leftbody.style.width='8px'
rightCon.style.marginLeft = '0px'
leftbodystate = false;
navPara=77;
function menuToggleUp(e) {
    e.stopPropagation()
    e.preventDefault()
    if (e.target !== menuToggle) {
        return
    }
    if (!menuToggleState){
        return;
    }
    let menuToggleTime1 = new Date().getTime()
    var t = menuToggleTime1 - menuToggleTime
    console.log('menuToggleUp:  ' + t)
    if (t > 600) {

    } else {
        if (deltaMenuToggleX!=null){
            return;
        }
        if (leftbodystate) {
            leftbody.style.display = 'none'
            // leftbody.style.width='8px'
            rightCon.style.marginLeft = '0px'
            leftbodystate = false;
            navPara=77;
            document.querySelector("#showPicC").style.left = null
        } else {
            leftbody.style.display = null
            // leftbody.style.width='8px'
            // rightCon.style.marginLeft = '77px'
            leftbodystate = true;
            document.querySelector("#showPicC").style.left = "78px"
            // navPara=0;
        }
        // menuToggle.style.display='flex'
        menuToggle.classList.toggle('active')

        // sidebar.classList.toggle('active')
    }
    // menuToggle.style.transform='scale(0.8)'
    // menuToggle.style.width='50px';
    // menuToggle.style.height= '50px';
    menuToggle.style.borderRadius = '0px'
    clearTimeout(menuToggleSetTime)
    menuToggleState=false
}
function menuToggleMove(e) {
    e.stopPropagation()
    e.preventDefault()
    if (e.target !== menuToggle) {
        return
    }
    clearTimeout(menuToggleSetTime)
    // 计算拖动的距离
    deltaMenuToggleX = e.touches[0].clientX - menuToggleX;
    deltaMenuToggleY = e.touches[0].clientY - menuToggleY;

    // 在这里处理拖动逻辑，比如更新元素位置
    menuToggle.style.transform = `translate3d(${deltaMenuToggleX}px,${deltaMenuToggleY}px, 0)`;
    menuToggle.style.borderRadius = '0px'
    menuToggleState=false
    deltaMenuToggleX=null
    deltaMenuToggleY=null
}
function menuToggleShow() {
    leftbody.style.display = ''
    // leftbody.style.width='8px'
    rightCon.style.marginLeft = '77px'
    leftbodystate = true;
    navPara=0;
}
function menuToggleHide() {
    leftbody.style.display = 'none'
    // leftbody.style.width='8px'
    rightCon.style.marginLeft = '2px'
    leftbodystate = false;
    navPara=77;
}
// menuToggle.
