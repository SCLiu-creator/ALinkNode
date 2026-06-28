let SelfIFP=document.querySelector("#selfInterface")

SelfIFP.parentNode.addEventListener('click',selfShow)
console.log('SelfIFP')
let showState=false
function selfShow(e) {
    if (e.target!==this){
        return
    }
    if (showState){
        SelfIFP.style.display='none'
        showState=false
    }else {
        SelfIFP.style.display='initial'
        showState=true
    }

}

let SelfPage=document.querySelector("#selfInterface")

let selfbackground=document.querySelector("#selfbackground")
selfbackground.addEventListener('click',selfShow)

function selfShowBack(e) {
    if (showState){
        SelfIFP.style.display='initial'
    }else {
        SelfIFP.style.display='none'
    }

}

let selfbcakbox = document.querySelector("#selfbcakbox")
selfbcakbox.style.backgroundImage = "url(http://" + address + ":" + port + "/map/SelfPage/getBackPic)"
selfbcakbox.style.backgroundSize = 'cover';
selfbcakbox.style.backgroundPosition = 'center';

let selfsetbp=document.querySelector("#selfbgInput")
selfsetbp.addEventListener('change',selectpic)
selfsetbp.addEventListener('click',selectpic)

function selectpic(e) {
    const fileList = e.target.files;
    // 确保只有一个文件被选中
    if (fileList.length > 0) {
        // 获取选中的文件
        const file = fileList[0];
        // 使用 fetch 发送文件到服务器
        fetch("http://" + address + ":" + port +"/map/SelfPage/UpBackPic", {
            method: 'POST',
            body: file, // 文件作为请求体发送
            headers: {
                'Content-Type': 'multipart/form-data', // 设置正确的 Content-Type
                // 'Content-Disposition': 'form-data', // 设置表单数据
            },
        })
            .then(response => response.json()) // 解析响应内容
            .then(data => {
                console.log('File uploaded:', data);
                let box = document.querySelector("#selfbcakbox");
                setBack(box,"/map/SelfPage/getBackPic")
            }
            ) // 处理上传成功的响应
            .catch(error => console.error('Error uploading file:', error)); // 处理上传失败的错误
    }

}

let selfPicBox = document.querySelector("#selfPicLabel")
selfPicBox.style.backgroundImage = "url(http://" + address + ":" + port + "/map/SelfPage/getUserPic)"
selfPicBox.style.backgroundSize = 'cover';
selfPicBox.style.backgroundPosition = 'center';

let setPic=document.querySelector("#selfPic")
setPic.addEventListener('change',selectUserpic)
setPic.addEventListener('click',selectUserpic)

function selectUserpic(e) {
    const fileList = e.target.files;
    // 确保只有一个文件被选中
    if (fileList.length > 0) {
        // 获取选中的文件
        const file = fileList[0];
        file.name;
        // 使用 fetch 发送文件到服务器
        fetch("http://" + address + ":" + port +"/map/SelfPage/UpUserPic", {
            method: 'POST',
            body: file, // 文件作为请求体发送
            headers: {
                'Content-Type': 'multipart/form-data', // 设置正确的 Content-Type
                // 'Content-Disposition': 'form-data', // 设置表单数据
            },
        }).then(response => response.json()) // 解析响应内容
         .then(data => {
            console.log('File uploaded:', data);
            let  box = document.querySelector("#selfPicLabel");
            setBack(box,"/map/SelfPage/getUserPic")
            var imageUrl = "http://" + address + ":" + port + url;
// 使用 fetch 获取图片数据
            fetch(imageUrl)
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    return response.blob();
                })
                .then(blob => {
                    // 创建一个 URL 对象，用于设置背景图像
                    var imageUrl = URL.createObjectURL(blob);
                    // 设置元素的背景图像
                    box.style.setProperty('--before-backgroundImage', "url(" + imageUrl + ")");
                    box.style.setProperty('--before-backgroundSize', 'cover');
                    // box.style.setProperty('--before-backgroundSize', 'contain');
                    box.style.setProperty('--before-backgroundPosition', 'center');
                })
                }
            ) // 处理上传成功的响应
            .catch(error => console.error('Error uploading file:', error)); // 处理上传失败的错误
    }

}


function setBack(box,url) {
// 获取图片 URL
    let imageUrl = "http://" + address + ":" + port + url;
// 使用 fetch 获取图片数据
    fetch(imageUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.blob();
        })
        .then(blob => {
            // 创建一个 URL 对象，用于设置背景图像
            var imageUrl = URL.createObjectURL(blob);

            // 设置元素的背景图像
            box.style.backgroundImage = "url(" + imageUrl + ")";
            document.body.style.backgroundImage = "url(" + imageUrl + ")";
            box.style.backgroundSize= 'contain';
            document.body.style.backgroundSize= 'contain';
            // selfbcakbox.style.backgroundImage = 'linear-gradient(to right, red, blue)';

// 或者设置背景图像为重复的渐变
//             selfbcakbox.style.backgroundImage = 'repeating-linear-gradient(to right, red, blue 10px, yellow 20px)';
//             // 设置背景图像大小为 auto
//             element.style.backgroundSize = 'auto';
// // 设置背景图像大小为 cover
//             element.style.backgroundSize = 'cover';
//
// // 设置背景图像大小为 contain
//             element.style.backgroundSize = 'contain';
//
// // 设置背景图像大小为指定的宽度和高度
//             element.style.backgroundSize = '100px 100px';
//
// // 设置背景图像大小为与元素区域相同的大小
//             selfbcakbox.style.backgroundSize = '100% 100%';
            selfbcakbox.style.backgroundSize = 'cover';
            selfbcakbox.style.backgroundPosition = 'center';
            // 注意：当图片不再需要时，应该释放 ObjectURL
            // 你可以使用 URL.revokeObjectURL(imageUrl) 方法来释放 URL
        })
        .catch(error => {
            console.error("Error fetching image:", error);
        });
}

let inputName = document.querySelector("#InputName")
fetch("http://" + address + ":" + port + "/map/Index/getUser").then(Response => Response.json())
    .then(json => {
        inputName.value = json['nickName']
        }
    )

inputName.addEventListener('click',showNameButton)
function showNameButton(e) {
    let InputNameButton = document.querySelector("#InputNameButton")
    InputNameButton.style.display='flex'
    InputNameButton.addEventListener('click',setSelfName)
    SelfPage.addEventListener('click',removeShowNameButton)
    SelfPage.addEventListener('torchup',removeShowNameButton)
}
function removeShowNameButton(e) {
    var cs=window.getComputedStyle(e.target)
    var sct=cs.getPropertyValue('--sn')
    if (sct!=="i"){
        let InputNameButton = document.querySelector("#InputNameButton")
        InputNameButton.style.display='none'
        SelfPage.removeEventListener('click',removeShowNameButton)
    }
}
function setSelfName(e) {
    e.preventDefault()
    e.stopPropagation()
    let InputName = document.querySelector("#InputName")
    fetch("http://" + address + ":" + port + "/map/SelfPage/setName?"+InputName.value)
        .then(Response => Response.text())
        .then(text => {
            InputName.value=text
            }
        ).finally(f=>{
        let InputNameButton = document.querySelector("#InputNameButton")
        InputNameButton.style.display='none'
    })
}

console.log("getip")
let showIpEle= document.querySelector("#showip")
showIpEle.addEventListener('click',showIp)
// window.addEventListener('load', function() {
//     showIp()
// });
fetch("http://" + address + ":" + port + "/map/show/showIp")
.then(response => {
    return response.json();
}).then(map => {
    showIpEle.innerHTML=''
    let ele=document.createElement("div")
    ele.setAttribute("class","text")
    ele.textContent="out: "+map["ip"]+":"+map["port"]
    showIpEle.appendChild(ele)
    ele=document.createElement("div")
    ele.setAttribute("class","text")
    ele.textContent="in : "+map["inip"]+":"+map["inport"]
    showIpEle.appendChild(ele)
})
function showIp(e) {
    loadstart()
    fetch("http://" + address + ":" + port + "/map/show/showIp")
        .then(response => {
            showIpEle.innerHTML=''
            return response.json();
        })
        .then(map => {
            let ele=document.createElement("div")
            ele.setAttribute("class","text")
            ele.textContent="out: "+map["ip"]+":"+map["port"]
            showIpEle.appendChild(ele)
            ele=document.createElement("div")
            ele.setAttribute("class","text")
            ele.textContent="in : "+map["inip"]+":"+map["inport"]
            showIpEle.appendChild(ele)
            if(map["ip"]>16 || map["inip"]>16){
                showIpEle.style.setProperty("font-size",'10px')
            }else {
                showIpEle.style.setProperty("font-size",'13px')
            }
            // showIpEle.textContent=s
        }).then(imageContainer=>{
        loadstop()
    }).catch(error => {
        console.error('There was a problem with the fetch operation:', error);
        loadstop()
    });
    return null;
}


let getQr=document.querySelector("#getQr")
getQr.addEventListener('click',showQr)
function showQr(e) {
        loadstart()
        fetch("http://" + address + ":" + port + "/map/show/showQR")
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                if (response.headers.get("Content-Length")===null
                    || response.headers.get("Content-Length")===undefined
                    || response.headers.get("Content-Length") === '0') {
                    throw new Error('null data');
                }
                return response.blob(); // 获取Blob对象
            }).then(blob => {
                // 创建一个对象URL指向Blob对象
                let imageUrl = URL.createObjectURL(blob);
                // 创建一个img元素并设置其src属性为对象URL
                let imgElement = document.createElement('img');
                imgElement.src = imageUrl;
                imgElement.setAttribute('class','picShow');
                let closeElement = document.createElement('div');
                closeElement.setAttribute('class','showPicClose')
                // closeElement.addEventListener('click',removeParentNode)
                closeElement.addEventListener('click', closePicNode)
                // let imageContainer = document.createElement('div');
                // imageContainer.setAttribute('class','showPic')
                let imageContainer0 = document.getElementById('showPicC');
                // if ()
                // imageContainer0.appendChild(imageContainer);
                // 将img元素添加到div中
                imageContainer0.appendChild(imgElement);
                imageContainer0.appendChild(closeElement);
                imageContainer0.style.display='flex';


                return imgElement;
            }).then(imageContainer=>{
            // let width=imageContainer.offsetWidth;
            // let height=imageContainer.offsetHeight;
            // let size=width/window.screenTop;
            // imageContainer.style.width=window.screenTop*0.8;
            // imageContainer.style.height=(height/size)*0.8;
            loadstop()
        }).catch(error => {
                console.error('There was a problem with the fetch operation:', error);
                loadstop()
            });
        return null;
}

let reloadE=document.querySelector("#reLoad")
reloadE.addEventListener('click',function (e) {
    loadstart()
    fetch("http://"+address+":" + port + "/map/SelfPage/reload").then(
        Response=>{
            showText("成功")
            loadstop()
        }
    ).catch(e=>loadstop())
    location.reload()
})
let clcEle=document.querySelector("#clcache")
clcEle.addEventListener('click',function (e) {
    fetch("http://"+address+":" + port + "/map/SelfPage/clcache").then(
        Response=>{
            showText1("成功")
        }
    )
})
let delUserE=document.querySelector("#delUser")
delUserE.addEventListener('click',function (e) {
    loadstart()
    fetch("http://"+address+":" + port + "/map/SelfPage/delUser").then(
        Response=>{
            showText("成功")
            loadstop()
        }
    ).catch(e=>loadstop())
})
