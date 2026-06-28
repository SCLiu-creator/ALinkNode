
let cloudeon=document.getElementById('CloudeOn')
cloudeon.addEventListener('click',cloudeOn)
let urlCloudOn="http://"+address+":" +port+"/map/ActionCloude/cloudeOn";
let urlCloudOff="http://"+address+":" +port+"/map/ActionCloude/cloudeOff";
let cloudstate=true;

function cloudeOn() {
    let body=document.querySelector(".create")
    let ssss=document.createElement("div");
    ssss.setAttribute("class","textbox1");
    let url;
    if (cloudstate){
        url=urlCloudOn
    }else {
        url=urlCloudOff
    }
    fetch(url)
        .then((Response)=> {
            if (Response.ok){
                if (Response.text()!== "成功")
                    ssss.innerText=Response.text()[Symbol.toStringTag].toString()
                ssss.innerText="成功"
                body.appendChild(ssss);
                window.setTimeout(function () {
                    body.removeChild(ssss);
                },1500)
                cloudstate=!cloudstate;
                if (cloudstate){
                    cloudeon.innerText="cloudeOn"
                }else {
                    cloudeon.innerText="cloudeOff"
                }
            }else {
                ssss.innerText="失败"
                body.appendChild(ssss);
                window.setTimeout(function () {
                    body.removeChild(ssss);
                },2000)
            }
            console.log(Response)})
}



let cloudClear=document.getElementById('CloudClear')
cloudClear.addEventListener('click',cloudeClear)

function cloudeClear() {
    let body = document.querySelector(".create")
    let ssss = document.createElement("div");
    ssss.setAttribute("class", "textbox1");

    fetch("http://" + address + ":" + port + "/map/ActionCloude/cloudeClear")
        .then((Response) => {
            if (Response.ok) {
                if (Response.text()[Symbol.toStringTag] !== "成功")
                    ssss.innerText = Response.text()[Symbol.toStringTag].toString()
                ssss.innerText = "成功"
                body.appendChild(ssss);
                window.setTimeout(function () {
                    body.removeChild(ssss);
                }, 1500)
                cloudstate = true;
                cloudeon.innerText = "cloudeOn"
            } else {
                ssss.innerText = "失败"
                body.appendChild(ssss);
                window.setTimeout(function () {
                    body.removeChild(ssss);
                }, 2000)
            }
            console.log(Response)
        })
}

let cloudUerChoose=document.getElementById('ChooseFile')

// let viewEle=document.createElement("fileView")
let viewEle=document.getElementById("showFile")
let fileSelect=new FileSelectInput(viewEle,cloudUerChoose)
// cloudUerChoose.appendChild(viewEle)
cloudUerChoose.addEventListener('click',fileSelect.fileMenu.bind(fileSelect))

function select(ele) {
    fetch("http://"+address+":" +port+"/map/ActionCloude/cloudeOff")
        .then((Response)=> {
            let body=document.querySelector(".create")
            let ssss=document.createElement("div")
            ssss.setAttribute("class","textbox1");
            ssss.innerText='成功'
            body.appendChild(ssss);
            window.setTimeout(function () {
                body.removeChild(ssss);
            },1500)
            console.log(Response)})
}

let editShowE=document.getElementById('editShow')
// getUserT.addEventListener("click",getUserOfShow)
editShowE.addEventListener("click",getShowEdit)
let userShowEdit
function getShowEdit(ele) {
    userShowEdit=new selfShowCD(
        document.querySelector("#showFile"),
        editShowE,
        userShowEditClose,)
    userShowEdit.fileMenu()
}
function userShowEditClose() {
    userShowEdit=null
}


let chatButton = document.querySelector("#chatbutton")

chatButton.addEventListener('click', getChat)
let chatstate=true
let chatload=true

let counter = 0;
let intervalId;

function loadChatHtml() {
    // if (chatload){
    //     $('#chat').load('htmls/chat.html'+ '#' + window.username);
    //     chatload=false;
    //     scrollToEle(document.querySelector("#chat"),7)
    //     let chat=document.getElementById('chat')
    //     intervalId= setInterval(function (){
    //         if(isEleInViewport(chat)){
    //             recT (chat)
    //         }
    //     }, 15000 );
    //     // document.querySelector("#chat").style.display='flex'
    // }
}

function getChat() {
    loadChatHtml()
    if (chatstate){
        let chat=document.querySelector("#chat")
        chat.style.display='flex'
        reloadchat(window.selectUserItem.dataset.username)
        intervalId= setInterval(function (){
            if(isEleInViewport(chat)){
                recT (chat)
            }
        }, 3000 );
        console.log(`添加任务`+intervalId);
        chatstate=false
        scrollToEle(chat,7)
    }else {
        document.querySelector("#chat").style.display='none'
        clearInterval(intervalId)
        console.log(`清除任务`+intervalId);
        chatstate=true
    }
    return false;
}

function recT(chat) {
    console.log(`这个任务已经执行了 ${++counter} 次`);
    reloadchat();
    // intervalId= setInterval(function (){
    //     if(isEleInViewport(chat)){
    //         recT ()
    //     }
    // }, 15000);
}

function chatlist(username) {


}
