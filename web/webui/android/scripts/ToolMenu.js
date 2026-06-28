// import SequentialFetchQueue from './sequentialFetchQueue.js';

// const fetchQueue = new SequentialFetchQueue();

// 随时添加请求到队列中
// fetchQueue.enqueue({ url: 'https://api.example.com/data1' }, (error, data) => {
//     if (error) {
//         console.error('请求失败:', error);
//     } else {
//         console.log('请求成功:', data);
//     }
// });

let CFele=document.querySelector("#chooseFile")
CFele.addEventListener("click",fileMenu)

let parentPath;
let chooseNodepath="";

function fileMenu(where, html) {
    let orgin = document.querySelector("#TMView");
    clearEleChildren(orgin);
    let choose = document.createElement('choose');
    let close = document.createElement('chooseclose');
    choose.setAttribute('class', 'optFile');
    close.setAttribute('class', 'closechoose');
    close.innerText='取消'
    close.addEventListener('click', closeCHV);
    choose.appendChild(close);
    orgin.appendChild(choose);
    fetch("http://" + address + ":" + port + "/map/show/openPathRoot", {withCredentials: true})
    .then((Response) => Response.json())
    .then((json) => {
        console.log(json);
        return json;
    }).then(jsondatas => {
        let choose = document.querySelector("#TMView .optFile");
        let contain = document.createElement('div');
        contain.setAttribute('class','contain')
        choose.appendChild(contain);
        for (let key in jsondatas) {
            let li = document.createElement("file");
            let ss = jsondatas[key];
            li.setAttribute("data", ss);
            li.dataset.locadpath = jsondatas[key]
            console.log(ss);
            li.setAttribute("data", ss);
            li.dataset.locadpath = jsondatas[key]
            var dv = document.createElement("dv");
            dv.innerText = ss;
            li.appendChild(dv);
            li.addEventListener('click', optPathP);
            contain.appendChild(li);
        }
        orgin.style.width='100vw'
        scrollToEle(orgin)
    });
}

function optPathP(e) {
    let param
    if (chooseNodepath===''){
        param=this.dataset.locadpath
    }else {
        param=chooseNodepath+"/"+ this.dataset.locadpath
    }

    chooseNodepath=param
    fetch("http://" + address + ":" + port + "/map/bitView/vPathList?" + param, {withCredentials: true})
        .then((Response) => Response.json())
        .then((json) => {
            console.log(json);
            return json;
        }).then(jsondatas => {
        let choose = document.querySelector("#TMView .optFile");
        let contain = document.querySelector('#TMView .optFile .contain');
        clearEleChildren(contain)
        choose.appendChild(contain);
        let path = document.createElement('docmentPath');
        path.innerText = chooseNodepath;
        contain.appendChild(path);
        path.addEventListener('click', toFileParent);

        for (var key in jsondatas) {
            var li
            if (jsondatas[key] === 'f') {
                li = document.createElement("file");
                li.setAttribute('class', 'file')
                li.setAttribute('filename', key)
                li.addEventListener('click', optFileB)
            } else {
                li = document.createElement("path");
                li.setAttribute('class', 'path')
                li.setAttribute('pathname', key)
                li.addEventListener('click', optPathP)
            }
            var ss = key;
            li.setAttribute("data", ss);
            li.dataset.locadpath = ss
            var dv = document.createElement("dv");
            dv.innerText = ss;
            li.appendChild(dv);
            contain.appendChild(li);
        }
    });
}

function optFileB(e) {
    let ele = e.target;
    while (ele.tagName !== 'FILE') {
        if (ele.parentNode === null) {
            e.target = null;
            return;
        }
        ele = ele.parentNode;
    }
    let orgin = document.querySelector("#TMView");
    clearEleChildren(orgin)
    let param= {};
    let fileName=chooseNodepath+'/'+e.target.textContent
    param["f"]=fileName
    param['s']=0;
    chooseNodepath=''
    param=JSON.stringify(param)
    fetch("http://" + address + ":" + port + "/map/bitView/bitViewFile?" + param, {withCredentials: true})
        .then((Response) => Response.json())
        .then(json => {
            let v=document.createElement('div')
            v.setAttribute('class','viewBit')
            v.dataset.filename=fileName
            // v.innerText=text.slice(1,-1)
            // let m
            let text=json['b']
            let pages=json['p']
            let ss=text.split(',');
            // for (let s of ss) {
            //     m=document.createElement('p')
            //     m.textContent=s
            //     v.appendChild(m)
            // }
            // 循环添加行
            // 创建一个表格元素
            let viewPage=document.createElement("div")
            viewPage.setAttribute('class','viewPage')
            let page=document.createElement("input")
            page.addEventListener('change',toFilePage)
            page.setAttribute('class','page')
            let pageLen=document.createElement("p")
            page.placeholder=0
            pageLen.innerText=pages
            viewPage.appendChild(page)
            viewPage.appendChild(pageLen)
            v.appendChild(viewPage)
            var table = document.createElement('table');
            table.dataset.page = 0
            // table.style.border = '1px'; // 设置表格边框，仅为了视觉效果
            for (let i = 0; i < 1280; i += 16) { // 每20个数字一行
                var tr = document.createElement('tr'); // 创建表格行
                // 循环添加单元格和数字
                for (let j = 0; j < 16 && i + j < 1280; j++) {
                    var td = document.createElement('input'); // 创建表格单元格
                    // td.textContent =ss[i + j + 1] ; // 设置单元格内容（数字）
                    td.placeholder=ss[i + j ]
                    td.dataset.num=i +j
                    td.addEventListener('input',editFile)
                    tr.appendChild(td); // 将单元格添加到行
                }
                table.appendChild(tr); // 将行添加到表格
            }
            v.appendChild(table)
            orgin.appendChild(v)
            // v.innerText=text
    });
}

function toFileParent(e) {
    if (e!==null && e!==undefined){
        if (e.target !== this) {
            return;
        }
    }

    fetch("http://" + address + ":" + port + "/map/bitView/openParentP?" + chooseNodepath, {withCredentials: true})
        .then((Response) => Response.json())
        .then((json) => {
            console.log(json);
            return json;
        }).then(jsondatas => {
        let choose = document.querySelector("#TMView .optFile");
        let contain = document.querySelector('#TMView .optFile .contain');
        clearEleChildren(contain)
        choose.appendChild(contain);
        let path = document.createElement('docmentPath');
        chooseNodepath=getLastSegment(chooseNodepath)
        path.innerText = chooseNodepath;
        contain.appendChild(path);
        path.addEventListener('click', toFileParent);

        for (var key in jsondatas) {
            var li
            if (jsondatas[key] === 'f') {
                li= document.createElement("file");
                li.setAttribute('class', 'file')
                li.setAttribute('filename', key)
                li.addEventListener('click', optFileB)
            } else {
                li= document.createElement("path");
                li.setAttribute('class', 'path')
                li.setAttribute('pathname', key)
                li.addEventListener('click', optPathP)
            }
            // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
            var ss = key;
            li.setAttribute("data", ss);
            li.dataset.locadpath = key
            var dv = document.createElement("dv");
            dv.innerText = ss;
            li.appendChild(dv);
            contain.appendChild(li);
        }
    });
}

function editFile(e) {
    let s=this.dataset.num
    let ele=document.querySelector('#TMView .viewPage .page').value
    let file=document.querySelector('#TMView .viewBit').dataset.filename
    let para={}
    para['s']=Number(s)+Number(ele)*1280
    para['f']=file
    para['b']=this.value
    para=JSON.stringify(para)
    fetch("http://" + address + ":" + port + "/map/bitView/bitEdit?" + para, {withCredentials: true})
        .then((Response) => Response.json())
        .then((json) => {
            console.log(json);
            return json;
        })
}
function toFilePage(e) {
    let file=document.querySelector('#TMView .viewBit').dataset.filename
    let para={}
    para['s']=Number(this.value)*1280
    para['f']=file
    para=JSON.stringify(para)
    fetch("http://" + address + ":" + port + "/map/bitView/bitViewFile?" + para, {withCredentials: true})
        .then((Response) => Response.json())
        .then((json) => {
            let v=document.querySelector('.viewBit')
            let table=document.querySelector('#TMView .viewBit table')
            let text=json['b']
            let ss=text.split(',');
            v.removeChild(table)
            table=document.createElement('table');
            // table.style.border = '1px'; // 设置表格边框，仅为了视觉效果
            for (let i = 0; i < 1280; i += 16) { // 每20个数字一行
                var tr = document.createElement('tr'); // 创建表格行
                // 循环添加单元格和数字
                for (let j = 0; j < 16 && i + j < 1280; j++) {
                    var td = document.createElement('input'); // 创建表格单元格
                    // td.textContent =ss[i + j + 1] ; // 设置单元格内容（数字）
                    td.placeholder=ss[i + j ]
                    td.dataset.num=i+j
                    td.addEventListener('input',editFile)
                    tr.appendChild(td); // 将单元格添加到行
                }
                table.appendChild(tr); // 将行添加到表格
            }
            v.appendChild(table)
        })
}
function closeCHV() {
    let node = this.parentNode;
    node.parentNode.removeChild(node);
    chooseNodepath='';
    document.querySelector("#TMView").style=null;

}

function getLastSegment(str) {
    const lastIndex = str.lastIndexOf('/');
    if (lastIndex === -1) {
        // 如果没有找到'/'，则直接返回原字符串
        return str;
    }
    return str.substring(0, lastIndex);
}
function clearEleChildren(element) {
    while (element.firstChild) {
        element.removeChild(element.firstChild);
    }
    console.log(`${element.id || 'The element'}'s all children have been cleared.`);
}
function clearEleChildrenClass(ele,str) {
    let child = ele.firstChild;
    while (child) {
        const nextSibling = child.nextElementSibling; // 先保存下一个兄弟节点
        if (child.nodeType === 1 && child.classList.contains(str)) { // 检查是否为元素节点并且包含指定的类
            ele.removeChild(child);
        }
        child = nextSibling; // 移动到下一个兄弟节点
    }
}

let upJar=document.getElementById('upJar')
upJar.addEventListener('change',selectJarUp)
upJar.addEventListener('click',selectJarUp)
function selectJarUp(e) {
    const fileList = e.target.files;
    // 确保只有一个文件被选中
    if (fileList.length > 0) {
        // 获取选中的文件
        const file = fileList[0];
        file.name;
        // 使用 fetch 发送文件到服务器
        fetch("http://" + address + ":" + port +"/map/file/upJar", {
            method: 'POST',
            body: file, // 文件作为请求体发送
            headers: {
                'Content-Type': 'multipart/form-data', // 设置正确的 Content-Type
                'name': file.name,
                },
        }).then(response => response.json()) // 解析响应内容
        .then(data => {
                console.log('File uploaded:', data);
            }
        ) // 处理上传成功的响应
        .catch(error => console.error('Error uploading file:', error)); // 处理上传失败的错误
    }

}

// document.getElementById('inputs').addEventListener('input', function(event) {
//     // 这里的 event.target 就是被编辑的元素
//     console.log('文本已更新:', event.target.innerText);
//
//     // 获取修改位置的一个简单方法（这里只是示例，可能不准确）
//     // 注意：这里的方法并不精确，只是用来展示思路
//     // 实际应用中可能需要更复杂的逻辑，比如通过保存前后状态的对比来推测修改位置
//
//     // 假设我们简单地通过选择范围来尝试获取位置（这可能并不总是可行，尤其是当进行大段修改时）
//     const selection = window.getSelection();
//     if (selection.rangeCount > 0) {
//         const range = selection.getRangeAt(0);
//         // 注意：这里的 startContainer 和 endOffset 并不是严格意义上的“修改位置”
//         // 它们只是当前选择范围的起始位置和容器
//         console.log('选择范围起始：', range.startContainer, '偏移：', range.startOffset);
//     } else {
//         console.log('没有选择范围');
//     }
//
//     // 注意：如果要精确获取每次修改的字符位置，可能需要使用更复杂的数据结构
//     // 比如，在每次输入前保存文本状态，并与当前状态进行比较
// });