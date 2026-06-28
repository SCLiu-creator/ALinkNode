let port = window.location.port
let address = window.location.hostname
let inp

loginUrl="http://"+address+":" + port + "/map/login/loginuser"

url=`http://${address}:${port}/map/Index/Loginlist`
fetch(url).//
then((Response)=>Response.json()).
then((json)=>{
    console.log(json);
    return json}).
then((json)=>{
    var sn=0;
    var loginchooselist=document.querySelector('.logincontainer .box');
    for(var user in json){
        createLoginUserDiv(json[user],loginchooselist,sn);
        sn=sn+1;
    }
    sn='';

    inp=createLoginUserDiv({nickName:'创建新用户',username:''},loginchooselist,sn);
    inp.removeEventListener('click',addLoginOnclick);
    let inp1=document.createElement("div")
    inp1.setAttribute("id","input");
    inp.addEventListener('click',createUserDiv)
})

// window.username='';
function createLoginUserDiv(user,box,sn){
    // let user=JSON.parse(userjson);
    var listdiv=document.createElement('div');
    listdiv.setAttribute('class','list');
    listdiv.setAttribute('onclick','addLoginOnclick');
    listdiv.addEventListener('click',addLoginOnclick);
    var columndiv=document.createElement('div');
    var contextdiv=document.createElement('div');

    columndiv.setAttribute('class','userColumn');
    columndiv.innerHTML='<img src="images/img02.png" alt=""></img>';
    contextdiv.setAttribute('class','content');
    var h1=document.createElement('h1');
    h1.setAttribute('class','sn');
    var h2=document.createElement('h2');
    var p=document.createElement('p');
    h1.innerHTML='<small>#</small>'+sn;
    h2.innerText=user['nickName'];
    box.appendChild(listdiv);
    listdiv.appendChild(columndiv);
    listdiv.appendChild(contextdiv);
    contextdiv.appendChild(h1)
    contextdiv.appendChild(h2)
    contextdiv.appendChild(p)

    // var p=document.createElement('p');
    p.innerText=user['username'];
    return listdiv;
}
fetch(`http://${address}:${port}/map/login/getState`).
    then((Response)=>Response.json()).
    then((json)=>{
        console.log(json);
        let but=document.querySelector('#loginChooseButton2 .circle0');
        let blueBlock0=document.querySelector('#loginChooseButton2 .blue-block0');
        if (json["ip"] ==="ipv6") {
            but.style.left='initial'
            but.style.right='0%'
            blueBlock0.style.background='#dceefc'
            fetch("http://"+address+":" + port + "/map/login/setIp?false")
                .then((Response)=>Response.text())
        }else {
            but.style.right='initial'
            but.style.left='0%'
            blueBlock0.style.background='#ffffff'
            fetch("http://"+address+":" + port + "/map/login/setIp?true")
                .then((Response)=>Response.text())
        }
        return json
    }).then((json)=>{

        })

function addLoginOnclick(e){
   var listelement=e.target;
    e.stopPropagation();
    while(listelement!==this){
        listelement=listelement.parentNode
    }
    var nodes=listelement.childNodes;
    nodes.forEach(element => {
        if(element.classList.contains('content')){
            element.childNodes.forEach((node)=>{
                if(node.className==='sn'){
                    var user=node.innerText
                    // user=user.split('#')[1]
                    user=user.replace(/#/g,'');
                    let rebody={};
                    var name;
                    element.childNodes.forEach(n1=>{
                        if(n1.tagName==='P'){
                            user=n1.innerText
                        }
                        if(n1.tagName==='H2'){
                            name=n1.innerText
                        }
                    })
                    rebody['user']=user
                    rebody['name']=name
                    fetch(loginUrl,{
                        method : 'post',
                        body:JSON.stringify(rebody)
                    }).then((Response)=>Response.text()).
                    then(text=>{
                        if (text==='""'||text==="error"){
                            let body=document.querySelector("BODY")
                            let ssss=document.createElement("div")
                                ssss.setAttribute("class","textbox");
                            ssss.innerText="登录请求超时"
                            body.appendChild(ssss);
                            window.setTimeout(function () {
                                body.removeChild(ssss);
                            },2000)
                            return ;
                        }
                        text=text.replace(/\"/g,"")

                        window.username=text;
                        const url = "http://"+address+":" + port + "/static/webui/main.html";
                        console.log(text,url);
                        window.history.replaceState({},'')
                        window.location.replace("http://"+address+":" + port + "/static/webui/main.html") ;
                        window.localUserName=text
                    })
                }
            })
        }
        element.childNode
    });
}

async function createUserDiv() {
    let doc=document.querySelector('.non')
    await fetch("http://" + address + ":" + port + "/static/webui/htmls/inputBox.html")
        .then(Response=>Response.text())
        .then(text=>{
            this.innerHTML=text;
            this.removeEventListener('click',createUserDiv)
            let body=document.querySelector('body')
            body.addEventListener('click',resetCreate);
            this.addEventListener('onkeydown',enterlis)
            console.log("aaaaa")
        })
    // this.innerHTML
    // ('#input').load('htmls/inputBox.html');
}
function resetCreate(e) {
    if (e.target!=this){return ;}
    let pn=inp.parentNode
    pn.removeChild(inp)
    inp=createLoginUserDiv({nickName:'创建新用户',username:''},pn,"");
    inp.removeEventListener('click',addLoginOnclick);
    let inp1=document.createElement("div")
    inp1.setAttribute("id","input");
    inp.addEventListener('click',createUserDiv)
    this.removeEventListener('click',resetCreate)
}


function enterlis() {
    if (window.event.keyCode==13){
        let node=document.querySelector('#myInput');
        let rebody={};

        rebody['user']=''
        rebody['name']=node.value
        fetch(loginUrl,{
            method : 'post',
            body:JSON.stringify(rebody)
        }).then((Response)=>Response.text()).
        then(text=>{
            text=text.replace(/\"/g,"")
            if (text==='""'||text==="error"){
                return
            }
            window.username=text;
            const url = "http://"+address+":" + port + "/static/webui/main.html";
            console.log(text,url);

            window.history.replaceState({},'')
            window.location.replace("http://"+address+":" + port + "/static/webui/main.html") ;
        })
    }
}
function rightlis() {
    let node=document.querySelector('#myInput');
    let rebody={};

    rebody['user']=''
    rebody['name']=node.value
    fetch(loginUrl,{
        method : 'post',
        body:JSON.stringify(rebody)
        })
        .then((Response)=>Response.text())
        .then(text=>{
            text=text.replace(/\"/g,"")
            if (text==='""'||text==="error"){
                return
            }
            window.username=text;
            const url = "http://"+address+":" + port + "/static/webui/main.html";
            console.log(text,url);

            window.history.replaceState({},'')
            window.location.replace("http://"+address+":" + port + "/static/webui/main.html") ;
        })
}

let lChoose=document.querySelector('#loginChooseButton1')
lChoose.addEventListener('click',loginChoose);
function loginChoose(){
    let but=document.querySelector('#loginChooseButton1 .circle0');
    let blueBlock0=document.querySelector('#loginChooseButton1 .blue-block0');
    if (but.style.left==='initial'){
        but.style.right='initial'
        but.style.left='0%'
        blueBlock0.style.background='#ffffff'
        loginUrl="http://"+address+":" + port + "/map/login/loginuser"
    }else {
        but.style.left='initial'
        but.style.right='0%'
        blueBlock0.style.background='#dceefc'
        loginUrl="http://"+address+":" + port + "/map/login/loginIn"
    }
}
let ipnode=document.querySelector('#loginChooseButton2')
ipnode.addEventListener('click',ipChoose);
function ipChoose(){
    let but=document.querySelector('#loginChooseButton2 .circle0');
    let blueBlock0=document.querySelector('#loginChooseButton2 .blue-block0');
    if (but.style.left=='initial'){
        but.style.right='initial'
        but.style.left='0%'
        blueBlock0.style.background='#ffffff'
        fetch("http://"+address+":" + port + "/map/login/setIp?true")
            .then((Response)=>Response.text())
    }else {
        but.style.left='initial'
        but.style.right='0%'
        blueBlock0.style.background='#dceefc'
        fetch("http://"+address+":" + port + "/map/login/setIp?false")
            .then((Response)=>Response.text())
    }
}
