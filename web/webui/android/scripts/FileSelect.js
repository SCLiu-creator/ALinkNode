class FileSelectInput {
    constructor(
        ele,
        eleEven,
        // selectFunction=optFileB,
        urlRoot = "/map/show/openPathRoot",
        urlPathList = "/map/bitView/vPathList?",
        urlParent = "/map/bitView/openParentP?",
        urlRight = "/map/CloudeChoose/selectFile"
    ) {
        this.ele = ele;
        this.eleEvent = eleEven
        this.urlRoot = urlRoot;
        this.urlPathList = urlPathList
        this.urlParent = urlParent
        this.urlRight = urlRight
        this.selectFunction = null
        // ele.style.position="fixed";
        // ele.style.height= "100vh";
        // ele.style.width= "100vw";
        // ele.style.maxWidth= "300px";
        // ele.style.top= "0";
        // ele.style.right= "0";
        // ele.style.display= "none";
        this.chooseNodepath = "";
        this.select = this.element;
    }


    fileMenu(e) {
        if (e.target !== this.ele && e.target !== this.ele.parentNode && e.target !== this.eleEvent) {
            return
        }
        clearEleChildren(this.ele);
        this.element = document.createElement('FileSelectView')
        this.element.setAttribute('class', 'FileSelectView')
        this.ele.appendChild(this.element)
        // this.ele.style.position= "absolute";
        this.ele.style.display = "flex";
        this.ele.style.height = "100%"
        this.ele.style.width = "100%"
        this.ele.style.marginLeft = "0"
        this.element.style.display = "initial";

        let optFile = document.createElement('div');
        optFile.setAttribute('class', 'optFile');
        let choose = document.createElement('choose');
        let close = document.createElement('chooseclose');
        close.setAttribute('class', 'closechoose');
        close.innerText = '取消'
        let bright = document.createElement('chooseright');
        bright.setAttribute('class', 'rightchoose')
        bright.innerText = '确认'

        close.addEventListener('click', this.closeCHV.bind(this));
        bright.addEventListener('click', this.rightchoose.bind(this));

        choose.appendChild(bright);
        choose.appendChild(close);
        optFile.appendChild(choose);
        this.element.appendChild(optFile);
        fetch("http://" + address + ":" + port + this.urlRoot, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = document.createElement('con');
            contain.setAttribute('class', 'contain')
            choose.appendChild(contain);
            for (var key in jsondatas) {
                var li = document.createElement("path");
                var ss = jsondatas[key];
                li.setAttribute("data", ss);
                li.dataset.locadpath = jsondatas[key]
                console.log(ss);
                li.setAttribute("data", ss);
                li.dataset.locadpath = jsondatas[key]
                var dv = document.createElement("dv");
                dv.innerText = ss;
                li.appendChild(dv);
                this.createInput(li)
                li.addEventListener('click', this.optPathP.bind(this));
                contain.appendChild(li);
            }
            scrollToEle(this.element,0)
        });
        return false
    }

    createInput(ele) {
        let i = document.createElement('input')
        i.setAttribute('type', "checkbox")
        i.setAttribute('id', "horns")
        i.setAttribute('name', "horns")
        ele.appendChild(i)
    }


    optPathP(e) {
        if (e.target.tagName !== "PATH" && e.target.tagName !== "FILE") {
            return
        }
        let param
        if (this.chooseNodepath === '') {
            param = e.target.dataset.locadpath
        } else {
            param = this.chooseNodepath + "/" + e.target.dataset.locadpath
        }
        this.chooseNodepath = param
        fetch("http://" + address + ":" + port + this.urlPathList + param, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = choose.querySelector('.contain');
            clearEleChildren(contain)
            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            path.innerText = this.chooseNodepath;
            contain.appendChild(path);
            path.addEventListener('click', this.toFileParent.bind(this));

            for (var key in jsondatas) {
                var li
                if (jsondatas[key] === 'f') {
                    li = document.createElement("file");
                    li.setAttribute('class', 'file')
                    li.setAttribute('filename', key)
                    li.addEventListener('click', this.selectFunction)
                } else {
                    li = document.createElement("path");
                    li.setAttribute('class', 'path')
                    li.setAttribute('pathname', key)
                    li.addEventListener('click', this.optPathP.bind(this))
                }
                var ss = key;
                li.setAttribute("data", ss);
                li.dataset.locadpath = ss
                var dv = document.createElement("dv");
                dv.innerText = ss;
                li.appendChild(dv);
                this.createInput(li)
                contain.appendChild(li);
            }
        });
    }

    toFileParent(e) {
        // if (e.target !== this) {
        //     return;
        // }
        fetch("http://" + address + ":" + port + this.urlParent + this.chooseNodepath, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = choose.querySelector('.contain');
            clearEleChildren(contain)
            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            this.chooseNodepath = getLastSegment(this.chooseNodepath)
            path.innerText = this.chooseNodepath;
            contain.appendChild(path);
            path.addEventListener('click', this.toFileParent.bind(this));

            for (let key in jsondatas) {
                let li
                if (jsondatas[key] === 'f') {
                    li = document.createElement("file");
                    li.setAttribute('class', 'file')
                    li.setAttribute('filename', key)
                    li.addEventListener('click', optFileB)
                } else {
                    li = document.createElement("path");
                    li.setAttribute('class', 'path')
                    li.setAttribute('pathname', key)
                    li.addEventListener('click', this.optPathP.bind(this))
                }
                // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
                var ss = key;
                li.setAttribute("data", ss);
                li.dataset.locadpath = key
                var dv = document.createElement("dv");
                dv.innerText = ss;
                li.appendChild(dv);
                this.createInput(li)
                contain.appendChild(li);
            }
        });
    }

    rightchoose() {
        let chooseinput = this.element.querySelectorAll("con input");
        let parentdoc = this.element.querySelector("docmentPath");
        let abpath = "";
        let arr = [];
        let arrE = [];
        if (parentdoc == null) {
            for (let i = 0; i < chooseinput.length; i++) {
                if (chooseinput[i].checked) {
                    arr.push(chooseinput[i].parentNode.dataset.locadpath);
                    arrE.push(chooseinput[i])
                }
            }
        } else {
            abpath = parentdoc.textContent;
            for (let i = 0; i < chooseinput.length; i++) {
                if (chooseinput[i].checked) {
                    arr.push(abpath + '/' + chooseinput[i].parentNode.dataset.locadpath);
                    arrE.push(chooseinput[i])
                }
            }
        }


        let choosejson = JSON.stringify(arr)
        fetch("http://" + address + ":" + port + this.urlRight, {
            method: 'post',
            body: choosejson
        }).then((Response) => Response.ok)
            .then(ok => {
                for (let i = 0; i < arrE.length; i++) {
                    arrE[i].checked = false
                }
            })
        return false
    };

    closeCHV() {
        let node = this.element;

        // clearEleChildren(this.element)
        node.parentNode.removeChild(node);
        // node.innerHTML=''
        this.ele.style = null
        // this.ele.style.width = null
        this.element.style.display = "none"
        this.chooseNodepath = '';
    }

}

class selfShowCD {
    constructor(
        ele,
        eleEven,
        eleFun,
        urlRoot = "/map/editShow/get",
        urlPathList = "/map/editShow/getShowPath?",
        urlParent = "/map/bitView/openParentP?",
        urlRight = "/map/editShow/delShowPath"
    ) {
        this.ele = ele;
        this.eleEvent = eleEven
        this.urlRoot = urlRoot;
        this.urlPathList = urlPathList
        this.urlParent = urlParent
        this.urlRight = urlRight
        this.selectFunction = null
        this.chooseNodepath = [];
        this.select = this.element;
    }

    fileMenu(e) {
        // if (e.target!==this.ele && e.target!==this.ele.parentNode && e.target!==this.eleEvent){
        //     return
        // }
        clearEleChildren(this.ele);
        this.element = document.createElement('FileSelectView')
        this.element.setAttribute('class', 'FileSelectView')
        this.ele.appendChild(this.element)
        // this.ele.style.position= "absolute";
        this.ele.style.display = "flex";
        this.ele.style.height = "100%"
        this.ele.style.width = "100%"
        this.ele.style.marginLeft = "0"
        this.element.style.display = "initial";
        let optFile = document.createElement('div');
        optFile.setAttribute('class', 'optFile');
        let choose = document.createElement('choose');
        let close = document.createElement('chooseclose');
        close.setAttribute('class', 'closechoose');
        close.innerText = '取消'
        let bright = document.createElement('chooseright');
        bright.setAttribute('class', 'rightchoose')
        bright.innerText = '删除'

        close.addEventListener('click', this.closeCHV.bind(this));
        bright.addEventListener('click', this.rightchoose.bind(this));

        choose.appendChild(bright);
        choose.appendChild(close);
        optFile.appendChild(choose);
        this.element.appendChild(optFile);
        fetch("http://" + address + ":" + port + this.urlRoot, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = document.createElement('con');
            contain.setAttribute('class', 'contain')
            choose.appendChild(contain);
            for (var key of jsondatas) {
                var li
                let time = document.createElement('p')
                let f = key['f']
                if (f === undefined || f === null) {
                    f = key['p']
                    li = document.createElement("path");
                    li.setAttribute('class', 'path')
                    li.setAttribute('pathname', key['p'])
                    // li.addEventListener('click', this.optPathP.bind(this))
                } else {
                    li = document.createElement("file");
                    li.setAttribute('class', 'file')
                    li.setAttribute('filename', key['f'])
                    // li.addEventListener('click', this.selectFunction)
                }

                li.setAttribute("data", f);
                li.dataset.locadpath = f
                var dv = document.createElement("dv");
                dv.innerText = f;
                li.appendChild(dv);

                time.innerText = key['t']
                // time.innerHTML=f
                li.appendChild(time)
                this.createInput(li)
                contain.appendChild(li);
            }
            scrollToEle(this.element,0)
        });
        return false
    }

    createInput(ele) {
        let i = document.createElement('input')
        i.setAttribute('type', "checkbox")
        i.setAttribute('id', "horns")
        i.setAttribute('name', "horns")
        ele.appendChild(i)
    }


    optPathP(e) {
        if (e.target.tagName !== "PATH" && e.target.tagName !== "FILE") {
            return
        }
        this.chooseNodepath.push(e.target.dataset.locadpath)
        let data = {}
        // param=param.split("/")
        data["p"] = JSON.stringify(this.chooseNodepath)
        fetch("http://" + address + ":" + port + this.urlPathList + JSON.stringify(data), {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = choose.querySelector('.contain');
            clearEleChildren(contain)
            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            path.innerText = this.chooseNodepath;
            contain.appendChild(path);
            path.addEventListener('click', this.toFileParent.bind(this));

            for (var key of jsondatas) {
                var li
                let time = document.createElement('p')
                let f = key['f']
                if (f === undefined || f === null) {
                    f = key['p']
                    li = document.createElement("path");
                    li.setAttribute('class', 'path')
                    li.setAttribute('pathname', key['p'])
                    li.addEventListener('click', this.optPathP.bind(this))
                } else {
                    li = document.createElement("file");
                    li.setAttribute('class', 'file')
                    li.setAttribute('filename', key['f'])
                    li.addEventListener('click', this.selectFunction)
                }

                li.setAttribute("data", f);
                li.dataset.locadpath = f
                var dv = document.createElement("dv");
                dv.innerText = f;
                li.appendChild(dv);

                time.innerText = key['t']
                // time.innerHTML=f
                li.appendChild(time)
                this.createInput(li)
                contain.appendChild(li);
            }
        });
    }

    toFileParent(e) {
        fetch("http://" + address + ":" + port + this.urlParent + this.chooseNodepath, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = choose.querySelector('.contain');
            clearEleChildren(contain)
            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            this.chooseNodepath = getLastSegment(this.chooseNodepath)
            path.innerText = this.chooseNodepath;
            contain.appendChild(path);
            path.addEventListener('click', this.toFileParent.bind(this));

            for (let key in jsondatas) {
                let li
                if (jsondatas[key] === 'f') {
                    li = document.createElement("file");
                    li.setAttribute('class', 'file')
                    li.setAttribute('filename', key)
                    li.addEventListener('click', optFileB)
                } else {
                    li = document.createElement("path");
                    li.setAttribute('class', 'path')
                    li.setAttribute('pathname', key)
                    li.addEventListener('click', this.optPathP.bind(this))
                }
                // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
                var ss = key;
                li.setAttribute("data", ss);
                li.dataset.locadpath = key
                var dv = document.createElement("dv");
                dv.innerText = ss;
                li.appendChild(dv);
                this.createInput(li)
                contain.appendChild(li);
            }
        });
    }

    rightchoose() {
        let chooseinput = this.element.querySelectorAll("con input");
        let param = {}
        let arrE = [];
        let arr = [];
        for (let i = 0; i < chooseinput.length; i++) {
            if (chooseinput[i].checked) {
                arrE.push(chooseinput[i])
                arr.push(chooseinput[i].parentNode.dataset.locadpath)
            }
        }

        param['p'] = JSON.stringify(arr)
        param['prex'] = JSON.stringify(this.chooseNodepath)
        fetch("http://" + address + ":" + port + this.urlRight, {
            method: 'post',
            body: JSON.stringify(param)}).
        then((Response) => Response.json()).
        then(j => {
                for (let i = 0; i < arrE.length; i++) {
                    arrE[i].checked = false
                }
                return j;
        }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = choose.querySelector('.contain');
            clearEleChildren(contain)
            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            path.innerText = this.chooseNodepath;
            contain.appendChild(path);
            path.addEventListener('click', this.toFileParent.bind(this));

            for (var key of jsondatas) {
                var li
                let time = document.createElement('p')
                let f = key['f']
                if (f === undefined || f === null) {
                    f = key['p']
                    li = document.createElement("path");
                    li.setAttribute('class', 'path')
                    li.setAttribute('pathname', key['p'])
                    li.addEventListener('click', this.optPathP.bind(this))
                } else {
                    li = document.createElement("file");
                    li.setAttribute('class', 'file')
                    li.setAttribute('filename', key['f'])
                    li.addEventListener('click', this.selectFunction)
                }

                li.setAttribute("data", f);
                li.dataset.locadpath = f
                var dv = document.createElement("dv");
                dv.innerText = f;
                li.appendChild(dv);

                time.innerText = key['t']
                // time.innerHTML=f
                li.appendChild(time)
                this.createInput(li)
                contain.appendChild(li);
            }
        })
        return false
    };

    closeCHV() {
        let node = this.element;

        // clearEleChildren(this.element)
        node.parentNode.removeChild(node);
        // node.innerHTML=''
        this.ele.style = null
        // this.ele.style.width = null
        this.element.style.display = "none"
        this.chooseNodepath = '';
    }

}

class UserShowFile {
    constructor(
        func,
        selectUser = window.selectUserItem.dataset.username,
        ele = document.querySelector("#showFile"),
        urlRoot = "/map/show/openPathRoot",
        urlPathList = "/map/userShow/getShowPath?",
        urlRight = ""
    ) {
        this.close = func
        this.ele = ele;
        this.urlRoot = urlRoot;
        this.urlPathList = urlPathList
        this.urlRight = urlRight;
        this.selectUser = selectUser
        this.element = null;
        this.chooseNodepath = [];
        this.chooseNodeFileCon = null
        this.select = this.element;
        this.nodes = []
        this.scrollPos={ scrollTop: 0, scrollLeft: 0 };
    }

    getUserOfShow(e) {
        let para = {}
        para["u"] = window.selectUserItem.dataset.username
        para["s"] = 0
        para["l"] = 30
        para = JSON.stringify(para)
        fetch("http://" + address + ":" + port + this.urlRoot + window.selectUserItem.dataset.username, {
            method: 'post', body: para
        }).then((Response) => {
            let body = document.querySelector(".create")
            let ssss = document.createElement("div")
            ssss.setAttribute("class", "textbox1");
            ssss.innerText = '成功'
            body.appendChild(ssss);
            window.setTimeout(function () {
                body.removeChild(ssss);
            }, 1500)
            console.log(Response)
            return Response.json()
        }).then(jsons => {
            clearEleChildren(this.ele);
            let show = document.querySelector("#showFile")
            show.setAttribute('style', '--my-sy: sy')

            let choose = document.createElement('choose');
            choose.setAttribute('class', 'optFile');
            let close = document.createElement('div');
            close.setAttribute('class', 'closechoose');
            close.innerText = '取消'

            close.addEventListener('click', this.close);
            close.addEventListener('click', this.closeCHV.bind(this));
            choose.appendChild(close);
            let showCon = document.createElement("div")
            showCon.setAttribute("class", 'userShowCon')
            this.element = showCon;
            this.ele.appendChild(this.element)
            this.ele.style.display = "flex";
            this.ele.style.height = "99%"
            // this.ele.style.width="100%"
            this.element.style.display = "flex";

            showCon.appendChild(choose);
            clearEleChildren(show)
            show.appendChild(showCon)
            let review=[]
            for (let data of jsons) {
                let ele
                let file = document.createElement('div')
                file.setAttribute("class", 'file')
                let time = document.createElement('p')
                let f = data['f']
                if (f === undefined || f === null) {
                    f = data['p']
                    ele = document.createElement("div")
                    ele.setAttribute("class", 'p')
                    ele.addEventListener('click', this.optPathP.bind(this))
                } else {
                    ele = document.createElement("div")
                    ele.setAttribute("class", 'f')
                    ele.addEventListener('click', this.rightchoose.bind(this))
                }
                review.push(ele)
                file.textContent = f
                time.innerText = data['t']
                // time.innerHTML=f
                ele.appendChild(file)
                ele.appendChild(time)
                showCon.appendChild(ele)
            }


            this.element = showCon;
            console.log(jsons)
            // scrollToEle(show,-5)

            // if (this.ele.offsetWidth<window.innerWidth-20){
            //     document.getElementById('listcontainer').style.width='200vw'
            // }


            function select(stri) {
                let arr=[]
                for (let ele of review) {
                    try {
                        let el=ele.querySelector('.file')
                        if (el==null){
                            arr.push(ele)
                            continue
                        }
                        let name=el.textContent

                        if (name.includes(stri)){
                            arr.push(ele)
                        }
                        showCon.removeChild(ele)
                    }catch (e) {
                        console.log(e)
                    }
                }

                for (let ele of arr){
                    showCon.appendChild(ele)
                }
            }
            selectFun=select
            function sortu() {
                // 获取所有子元素并转换为数组
                let childNodes = Array.from(showCon.children);

                // 对子元素进行排序和筛选
                // 假设我们根据子元素的data-value属性进行排序，并且筛选出属性值大于1的元素
                if(sortTime){
                    childNodes = childNodes
                        .sort((a, b) => {
                            let aa=a.querySelector("P")
                            let bb=b.querySelector("P")
                            if (!aa || !bb) {
                                return 0;
                            }

                            aa = aa.textContent.trim(); // 获取并清理时间字符串
                            bb = bb.textContent.trim();

                            // 将时间字符串解析为 Date 对象进行比较
                            const dateA = new Date(aa);
                            const dateB = new Date(bb);

                            if (isNaN(dateA) || isNaN(dateB)) {
                                return 0; // 如果解析失败，返回 0
                            }

                            return  dateA-dateB; // 比较时间
                        })
                }else {
                    childNodes = childNodes
                        .sort((a, b) => {
                            let el=a.querySelector('.file')
                            if (el==null){
                                return 0
                            }
                            let aa=getStrLast(el.textContent)
                            el=b.querySelector('.file')
                            if (el==null){
                                return 0
                            }
                            let bb=getStrLast(el.textContent)

                            if (aa==null||bb==null){
                                return 0
                            }
                            return aa.localeCompare(bb)
                        })
                }

                // .filter(child => child.getAttribute('name').includes(requiredString));
                while (showCon.firstChild) {
                    showCon.removeChild(showCon.firstChild);
                }
                childNodes.forEach(child => showCon.appendChild(child));
            }
            function sortd() {
                // 获取所有子元素并转换为数组
                let childNodes = Array.from(showCon.children);

                // 对子元素进行排序和筛选
                // 假设我们根据子元素的data-value属性进行排序，并且筛选出属性值大于1的元素
                if (sortTime){
                    childNodes = childNodes
                        .sort((a, b) => {
                            let aa=a.querySelector("P")
                            let bb=b.querySelector("P")
                            if (!aa || !bb) {
                                return 0;
                            }

                            aa = aa.textContent.trim(); // 获取并清理时间字符串
                            bb = bb.textContent.trim();

                            // 将时间字符串解析为 Date 对象进行比较
                            const dateA = new Date(aa);
                            const dateB = new Date(bb);

                            if (isNaN(dateA) || isNaN(dateB)) {
                                return 0; // 如果解析失败，返回 0
                            }

                            return  dateB-dateA; // 比较时间
                        })
                }  else {
                    childNodes = childNodes
                        .sort((a, b) => {
                            let el=a.querySelector('.file')
                            if (el==null){
                                return 0
                            }
                            let aa=getStrLast(el.textContent)
                            el=b.querySelector('.file')
                            if (el==null){
                                return 0
                            }
                            let bb=getStrLast(el.textContent)
                            if (aa==null||bb==null){
                                return 0
                            }
                            return bb.localeCompare(aa)
                        })
                }             // .filter(child => child.getAttribute('name').includes(requiredString));
                while (showCon.firstChild) {
                    showCon.removeChild(showCon.firstChild);
                }
                childNodes.forEach(child => showCon.appendChild(child));
            }
            sortFund=sortd
            sortFunu=sortu

            scrollToEle(this.ele)
            // document.getElementById('listcontainer').style.width='200vw';
        })
    }

    optPathP(e) {
        let target=e.target
        let path = target.querySelector('.file').textContent
        this.chooseNodepath.push(path)
        // path=this.chooseNodepath[0]
        // for (let i = 1; i < this.chooseNodepath.length; i++) {
        //     path=path+'/'+this.chooseNodepath[i]
        // }
        target.style.boxShadow='1px 2px 6px 2px rgba(24 193 255  / 68%)' ;
        path = JSON.stringify(this.chooseNodepath)
        let param = {}
        param['u'] = this.selectUser
        param['p'] = path
        param = JSON.stringify(param)
        viewLoadShow()
        fetch("http://" + address + ":" + port + this.urlPathList + param, {withCredentials: true})
            .then((Response) => Response.json())
            .then(jsons => {
                this.scrollPos = getScrollPositionRelativeToParent(target)
                this.element.dataset.scrollPos = JSON.stringify(this.scrollPos);
                viewLoadcl()
                this.nodes.push(this.element);
                let contain = document.createElement('div')
                contain.setAttribute("class", 'userShowCon')
                clearEleChildren(contain)

                let path = document.createElement('docmentPath');
                path.innerText = '...';
                contain.appendChild(path);
                path.addEventListener('click', this.dirToParent.bind(this));

                let pathShow = document.createElement('pathShow');
                pathShow.innerText = this.chooseNodepath;
                contain.appendChild(pathShow);
                pathShow.addEventListener('click', this.dirToParent.bind(this));

                let sonConn = document.createElement('div')
                sonConn.setAttribute("class", 'con')
                contain.appendChild(sonConn)
                let review=[]
                for (let data of jsons) {
                    let ele
                    let file = document.createElement('div')
                    file.setAttribute("class", 'file')
                    let time = document.createElement('p')
                    let f = data['f']
                    if (f === undefined || f === null) {
                        f = data['p']
                        ele = document.createElement("div")
                        ele.setAttribute("class", 'p')
                        ele.addEventListener('click', this.optPathP.bind(this))
                    } else {
                        ele = document.createElement("div")
                        ele.setAttribute("class", 'f')
                        ele.addEventListener('click', this.rightchoose.bind(this))
                    }
                    review.push(ele)
                    file.textContent = f
                    time.innerText = data['t']
                    // time.innerHTML=f
                    ele.appendChild(file)
                    ele.appendChild(time)
                    sonConn.appendChild(ele)
                }

                this.element.style.display = 'none';

                this.element = contain;
                this.ele.appendChild(this.element)

                function select(stri) {
                    let arr=[]
                    for (let ele of review) {
                        try {
                            let el=ele.querySelector('.file')
                            if (el==null){
                                arr.push(ele)
                                continue
                            }
                            let name=getStrLast(el.textContent)

                            if (name.includes(stri)){
                                arr.push(ele)
                            }
                            sonConn.removeChild(ele)
                        }catch (e) {
                            console.log(e)
                        }
                    }

                    for (let ele of arr){
                        sonConn.appendChild(ele)
                    }
                }
                selectFun=select
                function sortu() {
                    // 获取所有子元素并转换为数组
                    let childNodes = Array.from(sonConn.children);

                    // 对子元素进行排序和筛选
                    // 假设我们根据子元素的data-value属性进行排序，并且筛选出属性值大于1的元素
                    if (sortTime){
                        childNodes = childNodes
                            .sort((a, b) => {
                                let aa=a.querySelector("P")
                                let bb=b.querySelector("P")
                                if (!aa || !bb) {
                                    return 0;
                                }

                                aa = aa.textContent.trim(); // 获取并清理时间字符串
                                bb = bb.textContent.trim();

                                // 将时间字符串解析为 Date 对象进行比较
                                const dateA = new Date(aa);
                                const dateB = new Date(bb);

                                if (isNaN(dateA) || isNaN(dateB)) {
                                    return 0; // 如果解析失败，返回 0
                                }

                                return  dateA-dateB; // 比较时间
                            })
                    }else {
                        childNodes = childNodes
                            .sort((a, b) => {
                                let el=a.querySelector('.file')
                                if (el==null){
                                    return 0
                                }
                                let aa=getStrLast(el.textContent)

                                el=b.querySelector('.file')
                                if (el==null){
                                    return 0
                                }
                                let bb=getStrLast(el.textContent)

                                if (aa==null||bb==null){
                                    return 0
                                }
                                return aa.localeCompare(bb)
                            })
                    }

                    // .filter(child => child.getAttribute('name').includes(requiredString));
                    while (sonConn.firstChild) {
                        sonConn.removeChild(sonConn.firstChild);
                    }
                    childNodes.forEach(child => sonConn.appendChild(child));
                }
                function sortd() {
                    // 获取所有子元素并转换为数组
                    let childNodes = Array.from(sonConn.children);

                    // 对子元素进行排序和筛选
                    // 假设我们根据子元素的data-value属性进行排序，并且筛选出属性值大于1的元素
                    if (sortTime){
                        childNodes = childNodes
                            .sort((a, b) => {
                                let aa=a.querySelector("P")
                                let bb=b.querySelector("P")
                                if (!aa || !bb) {
                                    return 0;
                                }

                                aa = aa.textContent.trim(); // 获取并清理时间字符串
                                bb = bb.textContent.trim();

                                // 将时间字符串解析为 Date 对象进行比较
                                const dateA = new Date(aa);
                                const dateB = new Date(bb);

                                if (isNaN(dateA) || isNaN(dateB)) {
                                    return 0; // 如果解析失败，返回 0
                                }

                                return  dateB-dateA; // 比较时间
                            })
                    }else {
                        childNodes = childNodes
                            .sort((a, b) => {
                                let el=a.querySelector('.file')
                                if (el==null){
                                    return 0
                                }
                                let aa=getStrLast(el.textContent)
                                el=b.querySelector('.file')
                                if (el==null){
                                    return 0
                                }
                                let bb=getStrLast(el.textContent)
                                if (aa==null||bb==null){
                                    return 0
                                }
                                return bb.localeCompare(aa)
                            })
                    }
                   // .filter(child => child.getAttribute('name').includes(requiredString));
                    while (sonConn.firstChild) {
                        sonConn.removeChild(sonConn.firstChild);
                    }
                    childNodes.forEach(child => sonConn.appendChild(child));
                }
                sortFund=sortd
                sortFunu=sortu
            }).
        catch(e => {
            this.chooseNodepath.pop()
            this.nodes.pop()
            viewLoadcl()
        })
    }

    dirToParent(e) {
        if (e!==null&&e!==undefined){
            if (e.target.tagName!=="docmentpath".toUpperCase() && e.target.tagName!=="pathshow".toUpperCase() ){
                // return false
                return
            }
            e.stopPropagation()
            e.preventDefault()
        }

        this.element.parentNode.removeChild(this.element)
        this.element = this.nodes[this.nodes.length - 1]
        this.nodes.pop()
        this.chooseNodepath.pop()
        this.element.style.display = "flex"
        let scrollpos = this.element.dataset.scrollPos
        if (scrollpos!==undefined){
            try {
                scrollpos = JSON.parse(scrollpos)
                let sonCon = this.element.querySelector('.con')
                restoreScrollPosition(sonCon,scrollpos)
                // restoreScrollPosition(this.element,scrollpos)
            } catch (e) {
                console.warn('解析 scrollpos 失败', e);
            }
        }else {
            let sonCon = this.element.querySelector('.con')
            restoreScrollPosition(sonCon,this.scrollPos)
            // restoreScrollPosition(this.element,this.scrollPos)
        }

        this.scrollPos={ scrollTop: 0, scrollLeft: 0 };
        cancelTouchRS()
        cancelMouseRS()
        return false
    }
    fileToParent(e) {
        if (e!==null&&e!==undefined){
            if (e.target.className!=='view'){
                // return false
                return
            }
            e.stopPropagation()
            e.preventDefault()
        }

        // this.element.parentNode.removeChild(this.element)
        clearEleChildrenClass(this.ele,'view')
        this.element = this.nodes[this.nodes.length - 1]
        this.nodes.pop()
        // this.chooseNodepath.pop()
        this.element.style.display = "flex"
        let sonCon = this.element.querySelector('.con')
        restoreScrollPosition(sonCon,this.scrollPos)
        this.scrollPos={ scrollTop: 0, scrollLeft: 0 };
        cancelTouchRS()
        cancelMouseRS()
        return false
    }

    rightchoose(e) {
        let ele
        if (e instanceof Event) {
            ele = e.target.querySelector(".file")
            selectFileEle = ele.parentNode
        } else {
            // selectFileEle=e
            ele = e.querySelector(".file")
            if (ele == null) {
                this.chooseNodepath.pop()
            }
        }

        ele.style.boxShadow='1px 2px 6px 2px rgba(24 193 255  / 68%)' ;
        let file = ele.textContent;

        // let list=[]
        // for (let i = 0; i < this.chooseNodepath; i++) {
        //     list.push(this.chooseNodepath[i])
        // }
        // list.push(file)
        // this.chooseNodepath.push(file)
        let path = ''
        if (this.chooseNodepath.length !== 0) {
            path = this.chooseNodepath[0]
            for (let i = 1; i < this.chooseNodepath.length; i++) {
                path = path + '/' + this.chooseNodepath[i]
            }
        }
        path = path + "/" + file
        // let path=JSON.stringify(list)
        let param = {}
        param['u'] = this.selectUser
        param['p'] = path
        param = JSON.stringify(param)
        // viewLoadShow()

        let filename = file

        const picExtensions = [".jpg", ".jpeg", ".png", ".gif", ".webp", '.avif', '.svg', '.bmp', '.ico','.heic'];

// some() 方法：只要数组中有一个元素满足条件，就返回 true
        const lowerName = filename.toLowerCase();
        if (picExtensions.some(ext => lowerName.endsWith(ext))) {
            let frt = fetch("http://" + address + ":" + port + this.urlRight, {
                method: 'post',
                body: param
            })
            addListProcess(ele, param)
            frt.then((Response) => {
                if (Response.ok) {
                    let headers = Response.headers;
                    // 获取固定字段，例如Content-Type
                    let base64EncodedStr = headers.get('filename');
                    filename = decodeBase64ToUtf8(base64EncodedStr)
                    return Response.blob()
                } else {
                    throw new Error('Network response was not ok');
                }
            }).catch((e) => {
                console.log(e)
                this.chooseNodeFileCon.parentNode.removeChild(this.chooseNodeFileCon);
                // this.chooseNodepath.pop()
            }).then(async blob => {
                removeListProcess(ele)
                cancelTouchRS()
                cancelMouseRS()
                // viewLoadcl()
                this.scrollPos = getScrollPositionRelativeToParent(ele.parentElement)
                this.element.style.display = 'none'
                if (e instanceof Event) {
                    if (this.nodes[this.nodes.length - 1] !== this.element) {
                        this.nodes.push(this.element)
                    }
                    this.element.style.display = 'none'
                    // this.chooseNodeFile=this.element;
                } else {
                    // this.element
                    // this.nodes[this.nodes.length-1]=this.element
                    clearEleChildrenClass(this.ele, 'view')
                    this.chooseNodeFile = null;
                }

                let contain = document.createElement('div')
                contain.setAttribute('class', 'view')
                if (filename !== null && filename !== undefined) {
                    // contain.textContent = atob(filename);
                    contain.textContent = filename;
                }


                console.log("合法");
                let pic = document.createElement('div')
                pic.setAttribute('class', 'pic')

                contain.addEventListener('click', this.fileToParent.bind(this));

                let imageUrl = URL.createObjectURL(blob);
                if (path.toLowerCase().includes('.heic')) {
                    imageUrl = await convertHEICtoJPEG(blob)
                }
                // 设置元素的背景图像
                // pic.style.setProperty('--before-backgroundImage', "url(" + imageUrl + ")");
                // pic.style.backgroundImage="url(" + imageUrl + ")"
                // pic.style.setProperty('--before-backgroundSize', 'cover');
                // // box.style.setProperty('--before-backgroundSize', 'contain');
                // pic.style.setProperty('--before-backgroundPosition', 'center');
                pic = document.createElement('img')
                // pic.dataset.s = path;
                pic.addEventListener('wheel', this.tranBig.bind(pic))
                pic.dataset.path = path
                pic.addEventListener('touchstart', setTouchLongTimeShow)
                pic.addEventListener('mousedown', setMouseLongTimeShow)
                // pic.style.imageRendering = '-webkit-optimize-contrast';
                // pic.style.imageRendering = 'crisp-edges';
                // pic.style.imageRendering = 'pixelated';
                // 关键修复：强制开启 GPU 图层，减少渲染抖动
                pic.style.willChange = 'transform';
// 关键修复：针对 Android 的平滑渲染 hack
                pic.style.backfaceVisibility = 'hidden';
                pic.src = imageUrl
                pic.onload = () => {
                    URL.revokeObjectURL(imageUrl)
                    pic.style.maxWidth = '100%';
                    pic.style.maxHeight = '100%';
                //     const containerRect = contain.getBoundingClientRect();
                //     const imgRect = pic.getBoundingClientRect();
                //
                //     const naturalW = pic.naturalWidth;
                //     const naturalH = pic.naturalHeight;
                //
                //     const containerW = containerRect.width;
                //     const containerH = containerRect.height - 12;
                //     if(naturalW<containerW && naturalH<containerH)return;
                //     if (!naturalW || !containerW) return;
                //
                //     // 假设目标是 Contain (完整显示)
                //     const scaleContain = Math.min(containerW / naturalW, containerH / naturalH);
                //     // 假设目标是 Cover (填满)
                //     const scaleCover = Math.max(containerW / naturalW, containerH / naturalH);
                //
                //     const targetScale = Math.min(containerW / naturalW, containerH / naturalH);
                //
                //     // 2. 计算目标左上角坐标 (基于容器中心)
                //     // 目标中心 = 容器中心
                //     // 目标左上角 = 容器中心 - (自然宽 * 缩放 / 2)
                //     const containerCenterX = containerRect.left + containerW / 2;
                //     const containerCenterY = containerRect.top + containerH / 2;
                //
                //     const targetImgW = naturalW * targetScale;
                //     const targetImgH = naturalH * targetScale;
                //
                //     const targetLeft = containerCenterX - targetImgW / 2;
                //     const targetTop = containerCenterY - targetImgH / 2;
                //
                //     // 3. 获取当前左上角坐标
                //     // 注意：imgRect.left 是相对于视口的，我们需要相对于容器的吗？
                //     // 不，Translate 是相对于父级定位上下文的。
                //     // 如果 contain 是 relative/absolute，那么 translate 的参考系是 contain 的左上角。
                //     // 所以我们需要计算：(目标左上角相对于 contain) - (当前左上角相对于 contain)
                //
                //     const currentLeftRel = imgRect.left - containerRect.left;
                //     const currentTopRel = imgRect.top - containerRect.top;
                //
                //     const targetLeftRel = targetLeft - containerRect.left;
                //     const targetTopRel = targetTop - containerRect.top;
                //
                //     // 4. 计算 Translate
                //     // 因为 origin 是 0 0：
                //     // 变换后的位置 = 当前位置 + Translate
                //     // 我们希望：变换后的位置 = 目标位置
                //     // 所以：Translate = 目标位置 - 当前位置
                //     const tx = targetLeftRel - currentLeftRel;
                //     const ty = targetTopRel - currentTopRel;
                //
                //     console.log(`
                //   [调试 - 基于 Origin 0 0]
                //   容器：${containerW} x ${containerH}
                //   图片自然：${naturalW} x ${naturalH}
                //   目标缩放：${targetScale.toFixed(5)} (你的目标可能是 0.5731834，请对比)
                //
                //   当前相对左上角：(${currentLeftRel.toFixed(2)}, ${currentTopRel.toFixed(2)})
                //   目标相对左上角：(${targetLeftRel.toFixed(2)}, ${targetTopRel.toFixed(2)})
                //
                //   计算 Translate: (${tx.toFixed(2)}, ${ty.toFixed(2)})
                //   你的目标值：(250, 207.297)
                //
                //   如果计算结果与目标值不符，请检查：
                //   1. 图片自然尺寸是否正确？
                //   2. 容器尺寸是否正确？
                //   3. 是否使用了 Cover 模式而非 Contain？
                // `);
                //     // 应用
                //     pic.style.transform = `translate(${tx}px, ${ty}px) scale(${targetScale})`;
                //     // pic.style.transform = `translate3d(${tx}px, ${ty}px, 0) scale(${scaleFixed})`;
                }
                contain.appendChild(pic);

                this.ele.appendChild(contain)
                setTouchRS(pic);
                setMouseRS(pic)
                this.chooseNodeFileCon = contain;
            }).catch(e => {
                console.log(e)
                // this.dirToParent();
                clearEleChildrenClass(this.ele, 'view')
                // this.element.style.display = null
            })
            return false
        }


        const videoExtensions = [".mp4",".m4v",".webm", ".m3u8",".ts",  ".m4s" ];

        // some() 方法：只要数组中有一个元素满足条件，就返回 true
        if (videoExtensions.some(ext => lowerName.endsWith(ext))) {
            cancelTouchRS()
            cancelMouseRS()
            // viewLoadcl()
            this.scrollPos = getScrollPositionRelativeToParent(ele.parentElement)
            this.element.style.display = 'none'
            if (e instanceof Event) {
                if (this.nodes[this.nodes.length - 1] !== this.element) {
                    this.nodes.push(this.element)
                }
                this.element.style.display = 'none'
                // this.chooseNodeFile=this.element;
            } else {
                // this.element
                // this.nodes[this.nodes.length-1]=this.element
                clearEleChildrenClass(this.ele, 'view')
                this.chooseNodeFile = null;
            }


            let contain = document.createElement('div')
            contain.setAttribute('class', 'view')
            if (filename !== null && filename !== undefined) {
                // contain.textContent = atob(filename);
                contain.textContent = filename;
            }

            contain.insertAdjacentHTML('beforeend',
                '<div id="player-wrapper">\n' +

                '    <video id="videoPlayer" style="background: #000;" playsinline muted></video>\n' +
                '    <div id="controls">\n' +
                '        <button id="playBtn" disabled>加载中...</button>\n' +
                '        <input type="range" id="progressBar" value="0" step="0.1" disabled>\n' +
                '        <div class="time-info" id="timeDisplay">00:00 / 00:00</div>\n' +
                '        <div class="status" id="statusText">等待初始化</div>\n' +
                '    </div>\n' +
                '</div>' +
                '<div id="logBox"></div>'
            )
            this.ele.appendChild(contain)
            contain.addEventListener('click', this.fileToParent.bind(this));
// 配置
            const API_URL = "http://" + address + ":" + port + "/map/userShow/getVideoRange"; // 你的接口地址
            const CONFIG = {
                username: this.selectUser,
                filePath: path
            };
            // DOM 元素
            const video = document.getElementById('videoPlayer');
            const playBtn = document.getElementById('playBtn');
            const progressBar = document.getElementById('progressBar');
            const timeDisplay = document.getElementById('timeDisplay');
            const statusText = document.getElementById('statusText');
            const logBox = document.getElementById('logBox');
            const playerWrapper = document.getElementById('player-wrapper');
//             // 1. 定义 CSS 字符串 (你提供的原始 CSS)
            const playerStyles = `
            #player-wrapper { 
            width: 98%; background: #000; --my-tm: sy;--my-sy: sy;display: contents;
            border-radius: 8px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.6);
             margin: 10px auto; /*height: calc(100% - 150px)*/}
            .view video { width: 100%; display: block; }
            #controls { padding: 8px; background: #2c2c2c; display: flex;width: 100%; align-items: center; gap: 15px;--my-tm: sy;--my-sy: sy; }
            .view button { padding: 8px 20px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; transition: 0.2s; }
            .view button:hover { background: #0056b3; }
            .view button:disabled { background: #555; cursor: not-allowed; }
            .view input[type="range"] { flex-grow: 1; cursor: pointer; accent-color: #007bff; }
            .view .time-info { font-family: monospace; font-size: 14px; min-width: 120px; text-align: center; color: #fff;} /* 补充了白色字体以防看不见 */
            .view .status { font-size: 12px; color: #aaa; margin-left: auto; }

            /* 日志区域 */
            #logBox { width: 98%; margin: 10px 5px 5px 5px; background: #111; 
            border: 1px solid #333; border-radius: 4px; max-height:80px;
            overflow-y: auto; padding: 10px; font-family: 
            'Consolas', monospace; font-size: 11px; color: #0f0; }
            .view .log-entry { margin-bottom: 4px; border-bottom: 1px solid #222; padding-bottom: 2px; }
            .view .log-error { color: #ff4444; }
            .view .log-warn { color: #ffbb00; }
        `;
            // 3. 核心功能：通过 JS 动态注入 CSS
            function injectStyles(cssText) {
                if (!document.getElementById('dynamic-player-styles')) {
                    const styleElement = document.createElement('style');
                    styleElement.id = 'dynamic-player-styles';
                    styleElement.type = 'text/css';
                    styleElement.appendChild(document.createTextNode(cssText));
                    document.head.appendChild(styleElement);
                }
            }

            // 执行注入
            injectStyles(playerStyles);
//             // MSE 全局变量
//             let mediaSource = null;
//             let sourceBuffer = null;
//             let isAppending = false;
//             let pendingSeekTime = null;
//             let nextByteToLoad = 0; // 记录下一次连续播放应该请求的字节位置
//
//             let msePendingEndOfStream = true


            // 辅助：日志打印
            function log(msg, type = 'info') {
                const now = new Date().toLocaleTimeString();
                const div = document.createElement('div');
                div.className = `log-entry log-${type}`;
                div.textContent = `[${now}] ${msg}`;
                logBox.appendChild(div);
                logBox.scrollTop = logBox.scrollHeight;
                console.log(`[${type.toUpperCase()}] ${msg}`);
            }

            // 辅助：格式化时间
            function formatTime(seconds) {
                if (isNaN(seconds)) return "00:00";
                const m = Math.floor(seconds / 60);
                const s = Math.floor(seconds % 60);
                return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
            }

            // 构造视频 URL (方案一：原生 Range 请求)
            // 假设 apiConfig 包含 { apiUrl, username, filePath }
            const videoUrl = `${API_URL}?u=${encodeURIComponent(this.selectUser)}&p=${path}`;

            log(`准备加载视频: ${videoUrl}`, 'info');
            statusText.textContent = '正在连接...';

            // 关键设置：静音以允许自动播放
            video.currentTime = 0;
            // video.muted = true;
            video.src = videoUrl;
            video.load(); // 强制重新加载

            // --- 事件监听 ---

            // 1. 元数据加载完成 (最关键的一步)
            video.addEventListener('loadedmetadata', () => {
                log(`✅ 元数据加载成功 | 时长: ${formatTime(video.duration)}s | 尺寸: ${video.videoWidth}x${video.videoHeight}`, 'info');

                playBtn.disabled = false;
                playBtn.textContent = '播放';
                progressBar.disabled = false;
                progressBar.max = video.duration;
                timeDisplay.textContent = `00:00 / ${formatTime(video.duration)}`;
                statusText.textContent = '就绪 (点击播放或视频区域)';

                // 尝试自动播放 (因为设置了 muted，大概率会成功)
                // const playPromise = video.play();
                // if (playPromise !== undefined) {
                //     playPromise.then(() => {
                //         log('▶️ 自动播放成功 (静音模式)', 'info');
                //         playBtn.textContent = '暂停';
                //         statusText.textContent = '播放中...';
                //     }).catch(error => {
                //         log(`⚠️ 自动播放被拦截: ${error.message}. 请用户手动点击.`, 'warn');
                //         statusText.textContent = '点击播放';
                //     });
                // }
            });

            // 2. 可以开始播放
            video.addEventListener('canplay', () => {
                if (video.paused && playBtn.textContent === '播放') {
                    statusText.textContent = '可播放';
                }
            });

            // 3. 缓冲进度更新
            video.addEventListener('progress', () => {
                if (video.buffered.length > 0) {
                    const bufferedEnd = video.buffered.end(video.buffered.length - 1);
                    const percent = (bufferedEnd / video.duration) * 100;
                    // 这里可以扩展为显示缓冲条，目前先用原生控件逻辑
                    log(`📥 缓冲进度: ${Math.min(percent, 100).toFixed(1)}%`, 'info');
                }
            });

            // D. 播放/暂停 按钮逻辑
            function togglePlay() {
                if (video.paused) {
                    // 如果之前是静音自动播放，用户点击后是否取消静音？通常保持静音或让用户自己开声音
                    // 这里我们保持当前静音状态，或者如果你想让用户听到声音，可以在这里 unmute
                    video.muted = false;

                    const playPromise = video.play();
                    if (playPromise) {
                        playPromise.then(() => {
                            playBtn.textContent = '暂停';
                            statusText.textContent = '播放中';
                            log('▶️ 用户手动播放', 'info');
                        }).catch(err => {
                            log(`❌ 播放失败: ${err.message}`, 'error');
                        });
                    }
                } else {
                    video.pause();
                    playBtn.textContent = '播放';
                    statusText.textContent = '已暂停';
                    log('⏸️ 已暂停', 'info');
                }
            }

            playBtn.addEventListener('click', (e) => {
                e.stopPropagation(); // 防止触发容器的点击事件
                togglePlay();
            });

// E. 容器/视频区域点击播放 (提升体验)
            playerWrapper.addEventListener('click', () => {
                if (!playBtn.disabled) {
                    togglePlay();
                }
            });

            // 5. 进度条拖动 (Seek)
            let isSeeking = false;

            progressBar.addEventListener('input', () => {
                isSeeking = true;
                const seekTime = parseFloat(progressBar.value);
                timeDisplay.textContent = `${formatTime(seekTime)} / ${formatTime(video.duration)}`;
                statusText.textContent = '跳转中...';
            });

            progressBar.addEventListener('change', () => {
                isSeeking = false;
                const seekTime = parseFloat(progressBar.value);
                video.currentTime = seekTime;
                log(`⏩ 跳转至 ${formatTime(seekTime)}`, 'info');
                if (video.paused) {
                    // 如果之前是暂停，跳转后保持暂停，但更新按钮状态逻辑可根据需求调整
                    statusText.textContent = `已跳转至 ${formatTime(seekTime)}`;
                }
            });

            // 6. 时间更新 (同步进度条)
            video.addEventListener('timeupdate', () => {
                if (!isSeeking) {
                    progressBar.value = video.currentTime;
                    timeDisplay.textContent = `${formatTime(video.currentTime)} / ${formatTime(video.duration)}`;
                }
            });

            // 7. 播放结束
            video.addEventListener('ended', () => {
                playBtn.textContent = '重播';
                statusText.textContent = '播放结束';
                progressBar.value = 0;
                log('🏁 播放结束', 'info');
            });

            // 8. 错误处理
            video.addEventListener('error', (e) => {
                const err = video.error;
                let msg = '未知错误';
                if (err) {
                    switch(err.code) {
                        case err.MEDIA_ERR_ABORTED: msg = '用户取消加载'; break;
                        case err.MEDIA_ERR_NETWORK: msg = '网络错误 (检查后端 Range 支持)'; break;
                        case err.MEDIA_ERR_DECODE: msg = '解码错误 (格式不支持)'; break;
                        case err.MEDIA_ERR_SRC_NOT_SUPPORTED: msg = '视频源不支持或 URL 无效'; break;
                    }
                }
                log(`❌ 视频错误: ${msg} (Code: ${err?.code})`, 'error');
                statusText.textContent = '发生错误';
                playBtn.disabled = true;
                progressBar.disabled = true;
            });

            log('🎬 播放器初始化完成，等待元数据...', 'info');

//             // ================= 工具函数 =================
//             function log(msg, type = 'info') {
//                 const div = document.createElement('div');
//                 div.className = `log-entry ${type === 'error' ? 'log-error' : type === 'warn' ? 'log-warn' : ''}`;
//                 div.textContent = `[${new Date().toLocaleTimeString()}] ${msg}`;
//                 logBox.prepend(div);
//                 console.log(msg);
//             }
//
//             function formatTime(seconds) {
//                 if (!isFinite(seconds)) return "00:00";
//                 const m = Math.floor(seconds / 60);
//                 const s = Math.floor(seconds % 60);
//                 return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
//             }
//
//             // 核心算法：将时间转换为字节索引 (线性近似)
//             // 公式：字节 = (当前时间 / 总时长) * 总文件大小
//             function timeToByte(time) {
//                 if (CONFIG.totalFileSize === 0 || CONFIG.estimatedDuration === 0) return 0;
//                 const ratio = time / CONFIG.estimatedDuration;
//                 return Math.floor(ratio * CONFIG.totalFileSize);
//             }
//
//             // ================= 初始化流程 =================
//             async function initPlayer() {
//                 log("正在获取文件元数据 (大小)...");
//                 statusText.textContent = "获取元数据中...";
//
//                 try {
//                     // 策略：先发起一个 len=0 或 len=1 的请求，通过 Response Header 或特定逻辑获取文件总大小
//                     // 假设后端在 Content-Range 或自定义 Header 中返回总大小，或者我们专门有一个 head 请求
//                     // 这里模拟：发起一个只读 1 字节的请求来探测，或者假设有一个元数据接口
//                     // 如果你的后端没有返回总大小的机制，你需要硬编码 totalFileSize 或提供另一个接口
//
//                     // 方案 A: 尝试请求 0-1 字节，看能否从响应头获取总长 (标准 HTTP Range 行为，但你是 POST)
//                     // 由于是 POST，我们假设后端会在 JSON 响应或 Header 中告知总大小。
//                     // 这里为了演示，我们假设先调用一个轻量接口获取大小，或者你在 CONFIG 中硬编码。
//
//                     // 模拟获取元数据 (请替换为真实的元数据获取逻辑)
//                     // 如果后端完全不能提供任何元数据，你必须让用户输入或在代码里写死总大小和总时长
//                     // const metaResponse = await fetch(API_URL, {
//                     //     method: 'POST',
//                     //     headers: { 'Content-Type': 'application/json' },
//                     //     body: JSON.stringify({
//                     //         u: CONFIG.username,
//                     //         p: CONFIG.filePath,
//                     //         s: 0,
//                     //         l: 0 // 请求长度为 0，仅获取元数据 (需后端支持)
//                     //     })
//                     // });
//
//                     // 注意：如果后端不支持 l=0 返回元数据，你必须修改此处逻辑
//                     // 假设后端返回了 { totalSize: 12345678, duration: 120 }
//                     // 如果后端只返回流，那你必须在第一次请求前就知道总大小，否则无法做进度条！
//                     // *** 重要：此处假设你已经通过某种方式知道了 totalFileSize 和 duration ***
//                     // 为了代码能跑，我这里暂时用预设值，实际请从后端获取
//                     if (!CONFIG.totalFileSize) {
//                         log("警告：未获取到文件总大小，使用预估值 (可能导致进度条不准)", "warn");
//                         CONFIG.totalFileSize = CONFIG.estimatedDuration * (AVG_BITRATE_ESTIMATE / 8);
//                     }
//
//                     log(`文件总大小: ${(CONFIG.totalFileSize / 1024 / 1024).toFixed(2)} MB, 时长: ${CONFIG.estimatedDuration}s`);
//
//                     setupMSE();
//                 } catch (e) {
//                     log(`初始化失败: ${e.message}`, "error");
//                     statusText.textContent = "初始化失败";
//                 }
//             }
//             function handleStreamEnd() {
//                 log("🛑 流结束处理开始");
//                 statusText.textContent = "加载完成";
//
//                 // 1. 强制解锁按钮，确保不会卡在“加载中”
//                 playBtn.disabled = false;
//                 if (playBtn.textContent === "加载中...") {
//                     playBtn.textContent = "播放";
//                 }
//
//                 // 2. 通知 MediaSource 流结束
//                 // 这一步至关重要！浏览器收到后会自动计算总时长，进度条变实，允许播到最后一秒
//                 if (mediaSource && mediaSource.readyState === 'open') {
//                     try {
//                         mediaSource.endOfStream();
//                         log("✅ MediaSource 流已关闭 (endOfStream)");
//
//                         // 可选：更新 CONFIG.totalFileSize 为实际加载的总字节数，方便后续调试
//                         CONFIG.totalFileSize = nextByteToLoad;
//                         log(`修正文件总大小为: ${(CONFIG.totalFileSize/1024/1024).toFixed(2)}MB`);
//
//                     } catch (e) {
//                         log("关闭流失败: " + e.message, "error");
//                     }
//                 } else if (mediaSource && mediaSource.readyState === 'ended') {
//                     log("MediaSource 已经是结束状态");
//                 }
//             }
//             function setupMSE() {
//                 if (!MediaSource.isTypeSupported('video/mp4; codecs="avc1.42E01E, mp4a.40.2"')) {
//                     log("浏览器不支持 H.264 MP4，尝试 WebM...", "warn");
//                     // 可根据实际情况切换 codec
//                 }
//
//                 mediaSource = new MediaSource();
//                 video.src = URL.createObjectURL(mediaSource);
//
//                 mediaSource.addEventListener('sourceopen', () => {
//                     log("MediaSource 已打开");
//
//                     // 【方案 1】尝试自动检测支持的格式
//                     const candidates = [
//                         // 1. 常见 H.264 + AAC 组合 (覆盖 90% 的情况)
//                         'video/mp4; codecs="avc1.42E01E, mp4a.40.2"',
//                         'video/mp4; codecs="avc1.4D401E, mp4a.40.2"',
//                         'video/mp4; codecs="avc1.64001E, mp4a.40.2"',
//
//                         // 2. H.265 / HEVC (如果浏览器支持)
//                         'video/mp4; codecs="hvc1.1.6.L120.B0, mp4a.40.2"',
//                         'video/mp4; codecs="hev1.1.6.L120.B0, mp4a.40.2"',
//
//                         // 3. VP9 (WebM/MP4 容器都可能用)
//                         'video/webm; codecs="vp9, opus"',
//                         'video/mp4; codecs="vp09.00.10.08"',
//
//                         // 4.  终极兜底：不带 codecs 参数！
//                         // 让浏览器自己去猜！这是解决“未知格式”的关键。
//                         'video/mp4',
//                         'video/webm'
//                     ];
//
//                     let selectedMime = null;
//                     let sourceBufferCreated = false;
//                     for (const mime of candidates) {
//                         // 双重保险：先检查 isTypeSupported，再尝试 addSourceBuffer
//                         if (MediaSource.isTypeSupported(mime)) {
//                             try {
//                                 log(`尝试创建 SourceBuffer: ${mime} ...`);
//                                 sourceBuffer = mediaSource.addSourceBuffer(mime);
//
//                                 // 如果没报错，说明成功了！
//                                 selectedMime = mime;
//                                 sourceBufferCreated = true;
//                                 log(`✅ 成功使用格式: ${mime}`);
//                                 break; // 找到可用的就跳出循环
//                             } catch (e) {
//                                 log(`❌ 格式 ${mime} 创建失败: ${e.message}`, "warn");
//                                 // 继续尝试下一个
//                             }
//                         } else {
//                             log(`⚠️ 浏览器声明不支持: ${mime}`, "info");
//                         }
//                     }
//                     if (!sourceBufferCreated || !sourceBuffer) {
//                         log("🛑 致命错误：无法为任何已知格式创建 SourceBuffer", "error");
//                         statusText.textContent = "浏览器不支持此视频格式";
//                         playBtn.disabled = true;
//                         playBtn.textContent = "无法播放";
//                         return;
//                     }
//
//
//                     // 绑定事件
//                     sourceBuffer.addEventListener('updateend', onBufferUpdateEnd);
//                     sourceBuffer.addEventListener('error', (e) => {
//                         const err = sourceBuffer.error;
//                         log(`❗ SourceBuffer 运行时错误: ${err?.message || 'Unknown'}`, "error");
//                         isAppending = false;
//                         statusText.textContent = "解码错误";
//                         playBtn.disabled = false;
//                         playBtn.textContent = "重试";
//                     });
//                     // UI 更新
//                     progressBar.max = CONFIG.estimatedDuration;
//                     progressBar.disabled = false;
//                     playBtn.disabled = false;
//                     playBtn.textContent = "播放";
//                     statusText.textContent = "就绪";
//
//                     // 开始加载第一块
//                     nextByteToLoad = 0;
//                     loadChunk(0);
//                 });
//             }
//
// // 辅助函数：处理加载完成后的 UI 和 MSE 状态
//             function finishLoading() {
//                 statusText.textContent = "加载完成";
//                 playBtn.disabled = false;
//                 playBtn.textContent = "播放";
//
//                 // 如果 MSE 还开着，关闭它，让浏览器知道没更多数据了
//                 if (mediaSource && mediaSource.readyState === 'open') {
//                     try {
//                         mediaSource.endOfStream();
//                         log("MediaSource 流已关闭");
//                     } catch (e) {
//                         log("关闭流失败: " + e.message, "warn");
//                     }
//                 }
//             }
//             // ================= 核心：数据加载 =================
//             async function loadChunk(startByte) {
//                 // 1. 基础状态检查
//                 if (!mediaSource || mediaSource.readyState !== 'open' || !sourceBuffer) {
//                     log("MSE 未就绪，取消加载", "warn");
//                     return;
//                 }
//                 if (isAppending) {
//                     log("正在写入数据，跳过本次请求", "warn");
//                     return;
//                 }
//
//
//                 // --- 构造请求 ---
//                 // 注意：这里依然使用 CONFIG.totalFileSize 作为一个“最大上限”来防止请求溢出，
//                 // 但即使它不准，只要后端正常，我们靠返回长度来判断真实结束。
//                 // 如果 totalFileSize 完全不可信，可以将其设为一个极大的值 (如 Number.MAX_SAFE_INTEGER)
//                 const maxEnd = CONFIG.totalFileSize > 0 ? CONFIG.totalFileSize : Number.MAX_SAFE_INTEGER;
//                 const endByte = Math.min(startByte + CHUNK_SIZE, maxEnd);
//                 const requestLength = endByte - startByte;
//
//                 if (requestLength <= 0) {
//                     log("请求长度计算为 0，停止加载");
//                     handleStreamEnd();
//                     return;
//                 }
//
//                 statusText.textContent = `加载: ${(startByte/1024/1024).toFixed(1)}MB...`;
//                 log(`请求: start=${startByte}, len=${requestLength}`);
//
//                 try {
//                     const response = await fetch(API_URL, {
//                         method: 'POST',
//                         headers: { 'Content-Type': 'application/json' },
//                         body: JSON.stringify({
//                             u: CONFIG.username,
//                             p: CONFIG.filePath,
//                             s: startByte,
//                             l: requestLength
//                         })
//                     });
//
//                     if (!response.ok) {
//                         throw new Error(`HTTP ${response.status}`);
//                     }
//
//                     const arrayBuffer = await response.arrayBuffer();
//                     const receivedLength = arrayBuffer.byteLength;
//
//                     // ================= 🎯 核心判断逻辑 =================
//                     // 只要 返回长度 < 请求长度，就认定是文件末尾 (EOF)
//                     const isEOF = receivedLength < requestLength;
//
//                     if (receivedLength === 0) {
//                         log("⚠️ 收到空数据块，视为文件结束或异常", "warn");
//                         handleStreamEnd();
//                         return;
//                     }
//
//                     log(`✅ 收到数据: ${(receivedLength/1024).toFixed(1)}KB ${isEOF ? '(🏁 文件末尾)' : ''}`);
//
//                     // --- 写入数据 ---
//                     isAppending = true;
//                     sourceBuffer.appendBuffer(arrayBuffer);
//
//                     // --- 更新下一次加载位置 ---
//                     // 无论是否 EOF，都累加实际收到的字节数
//                     if (pendingSeekTime === null) {
//                         nextByteToLoad = startByte + receivedLength;
//                     }
//
//                     // --- 标记结束 ---
//                     if (isEOF) {
//                         log("🎉 检测到文件结束 (返回 < 请求)，准备关闭流");
//                         window._msePendingEndOfStream = true;
//                         // 注意：endOfStream 必须在 updateend 事件中调用，不能在这里直接调
//                     }
//
//                 } catch (err) {
//                     log(`❌ 请求失败: ${err.message}`, "error");
//                     isAppending = false;
//                     statusText.textContent = "加载错误";
//                     playBtn.disabled = false;
//                     playBtn.textContent = "重试";
//                 }
//             }
//
//             function onBufferUpdateEnd() {
//                 // 1. 安全检查
//                 if (!mediaSource || !sourceBuffer) {
//                     isAppending = false;
//                     return;
//                 }
//
//                 // 如果 MSE 已经结束，不再处理
//                 if (mediaSource.readyState === 'ended') {
//                     isAppending = false;
//                     return;
//                 }
//
//                 isAppending = false;
//                 log("数据写入完成");
//
//                 // ================= 🎯 处理文件结束标志 =================
//                 if (msePendingEndOfStream) {
//                     msePendingEndOfStream = false;
//                     handleStreamEnd();
//                     return; // 结束后直接返回，不再尝试预加载
//                 }
//                 // --- UI 状态解锁 (针对首次加载或小文件) ---
//                 // 如果按钮还卡在“加载中...”，且已经有数据了，就解锁它
//                 if (playBtn.textContent === "加载中..." && sourceBuffer.buffered.length > 0) {
//                     const end = sourceBuffer.buffered.end(sourceBuffer.buffered.length - 1);
//                     if (end > 0.5) { // 只要有超过 0.5 秒的数据
//                         log("✅ 初始缓冲就绪，解锁播放按钮");
//                         playBtn.disabled = false;
//                         playBtn.textContent = "播放";
//                         statusText.textContent = "就绪";
//                     }
//                 }
//
//                 // 3. 首次加载解锁 UI (防止小文件卡住)
//                 if (playBtn.textContent === "加载中..." && sourceBuffer.buffered.length > 0) {
//                     const end = sourceBuffer.buffered.end(sourceBuffer.buffered.length - 1);
//                     if (end > 1.0) { // 只要有超过1秒的数据
//                         playBtn.disabled = false;
//                         playBtn.textContent = "播放";
//                         statusText.textContent = "就绪";
//                         log("初始缓冲足够，解锁播放按钮");
//                     }
//                 }
//
//
//                 // 处理 Seek 后的跳转
//                 if (pendingSeekTime !== null) {
//                     // 再次双重检查
//                     if (mediaSource.readyState === 'open') {
//                         log(`执行跳转至: ${pendingSeekTime.toFixed(1)}s`);
//
//                         // 确保 currentTime 设置在缓冲范围内，避免报错
//                         try {
//                             video.currentTime = pendingSeekTime;
//
//                             // 跳转成功后尝试播放
//                             const playPromise = video.play();
//                             if (playPromise !== undefined) {
//                                 playPromise.catch(error => {
//                                     log("自动播放失败 (可能是用户未交互或源无效): " + error.message, "warn");
//                                     // 如果播放失败，保持暂停状态，让用户手动点击
//                                     playBtn.textContent = "播放";
//                                 });
//                             }
//                         } catch (e) {
//                             log("设置 currentTime 失败: " + e.message, "error");
//                         }
//
//                         pendingSeekTime = null;
//                         statusText.textContent = "播放中";
//                         playBtn.textContent = "暂停";
//                     }
//                 }
//
//                 // --- 预加载逻辑 (仅在非结束状态下执行) ---
//                 try {
//                     const bufferedEnd = sourceBuffer.buffered.length > 0
//                         ? sourceBuffer.buffered.end(sourceBuffer.buffered.length - 1)
//                         : 0;
//
//                     // 只有当播放头接近缓冲区末尾时，才加载下一段
//                     // 此时不再依赖 totalFileSize 判断是否结束，因为如果真是最后一段，
//                     // 下一次 loadChunk 会通过 returned < requested 自动触发结束
//                     if (video.currentTime > bufferedEnd - 5) {
//                         log(`触发预加载 (当前:${video.currentTime.toFixed(1)}, 缓冲尾:${bufferedEnd.toFixed(1)})`);
//                         loadChunk(nextByteToLoad);
//                     }
//                 } catch (e) {
//                     log("预加载检查出错", "warn");
//                 }
//             }
//
//             // ================= 拖拽 Seek 逻辑 =================
//             progressBar.addEventListener('input', (e) => {
//                 const t = parseFloat(e.target.value);
//                 timeDisplay.textContent = `${formatTime(t)} / ${formatTime(CONFIG.estimatedDuration)}`;
//             });
//
//             progressBar.addEventListener('change', (e) => {
//                 const targetTime = parseFloat(e.target.value);
//                 log(`用户拖拽进度条至: ${targetTime}s`);
//                 handleSeek(targetTime);
//             });
//
//             function handleSeek(targetTime) {
//                 // 【关键修复 1】检查 MSE 是否还活着
//                 if (!mediaSource || mediaSource.readyState !== 'open' || !sourceBuffer) {
//                     log("MSE 未就绪，无法跳转", "warn");
//                     return;
//                 }
//
//                 log(`用户拖拽进度条至: ${targetTime}s`);
//
//                 // 1. 计算目标字节位置
//                 const targetByte = timeToByte(targetTime);
//
//                 // 2. 检查是否已在缓冲区内 (安全访问 buffered)
//                 let inBuffer = false;
//                 try {
//                     // 再次确认 sourceBuffer 有效，防止在循环中被移除
//                     if (sourceBuffer.buffered.length > 0) {
//                         for (let i = 0; i < sourceBuffer.buffered.length; i++) {
//                             if (targetTime >= sourceBuffer.buffered.start(i) && targetTime <= sourceBuffer.buffered.end(i)) {
//                                 inBuffer = true;
//                                 break;
//                             }
//                         }
//                     }
//                 } catch (e) {
//                     log("读取 buffered 失败，视为不在缓冲区内", "warn");
//                     inBuffer = false;
//                 }
//
//                 if (inBuffer) {
//                     log("目标时间在缓冲区内，直接跳转");
//                     video.currentTime = targetTime;
//                     // 如果之前是暂停状态，用户拖拽后通常希望继续播放，可根据需求调整
//                     if (video.paused) video.play().catch(e => {});
//                     return;
//                 }
//
//                 // 3. 不在缓冲区内：需要清除旧数据并请求新数据
//                 log("目标不在缓冲区，准备重新加载...");
//
//                 // 停止当前正在进行的写入
//                 if (sourceBuffer.updating) {
//                     try { sourceBuffer.abort(); } catch(e) {}
//                 }
//
//                 // 清除所有缓冲 (简单策略)
//                 try {
//                     // 注意：remove 是异步的，但我们会立刻发起新请求
//                     for (let i = sourceBuffer.buffered.length - 1; i >= 0; i--) {
//                         sourceBuffer.remove(sourceBuffer.buffered.start(i), sourceBuffer.buffered.end(i));
//                     }
//                 } catch (e) {
//                     log("清除缓冲时出错: " + e.message, "error");
//                 }
//
//                 // 4. 计算安全的起始字节 (向前多拉取几秒)
//                 const safetyBytes = Math.floor((SEEK_SAFETY_MARGIN_SEC / CONFIG.estimatedDuration) * CONFIG.totalFileSize);
//                 const safeStartByte = Math.max(0, targetByte - safetyBytes);
//
//                 // 更新全局指针
//                 nextByteToLoad = safeStartByte + CHUNK_SIZE;
//
//                 // 5. 设置挂起跳转时间
//                 pendingSeekTime = targetTime;
//
//                 // 6. 暂停视频，等待数据
//                 video.pause();
//                 statusText.textContent = "缓冲中...";
//
//                 // 7. 发起请求
//                 loadChunk(safeStartByte);
//             }
//
//             // 播放按钮
//             playBtn.addEventListener('click', () => {
//                 // 检查源是否有效
//                 if (!video.src || mediaSource?.readyState !== 'open') {
//                     log("视频源未就绪，无法播放", "warn");
//                     return;
//                 }
//                 if (video.paused) {
//                     let targetTime = video.currentTime;
//                     let canPlay = false;
//                     if (sourceBuffer && sourceBuffer.buffered.length > 0) {
//                         const start = sourceBuffer.buffered.start(0);
//                         const end = sourceBuffer.buffered.end(sourceBuffer.buffered.length - 1);
//                         if (targetTime >= start && targetTime <= end) {
//                             canPlay = true;
//                         } else {
//                             // 如果不在，强制跳到缓冲区起始位置
//                             log(`当前时间 ${targetTime} 不在缓冲区 [${start}, ${end}]，修正为 ${start}`, "warn");
//                             targetTime = start;
//                             video.currentTime = start; // 先设置时间
//                             canPlay = true;
//                         }
//                     } else if (nextByteToLoad === 0) {
//                         // 如果还没加载任何数据，且 nextByteToLoad 是 0，说明还没开始加载
//                         log("暂无数据，开始加载第一段...", "warn");
//                         loadChunk(0);
//                         statusText.textContent = "加载中...";
//                         return;
//                     }
//                     // 检查是否有数据可播
//                     let hasData = false;
//                     try {
//                         if (sourceBuffer && sourceBuffer.buffered.length > 0) {
//                             const end = sourceBuffer.buffered.end(sourceBuffer.buffered.length - 1);
//                             if (video.currentTime < end) {
//                                 hasData = true;
//                             }
//                         }
//                     } catch(e) {}
//
//                     if (!hasData && nextByteToLoad < CONFIG.totalFileSize) {
//                         log("点击播放但无数据，触发加载...", "warn");
//                         // 如果没数据，先加载
//                         loadChunk(nextByteToLoad);
//                     }
//
//                     if (canPlay) {
//                         setTimeout(() => {
//                             const promise = video.play();
//                             if (promise) {
//                                 promise.catch(err => {
//                                     log("播放失败: " + err.message, "error");
//                                     if (err.message.includes("interrupted")) {
//                                         // 如果是 interrupted，尝试再次重试
//                                         setTimeout(() => video.play().catch(e=>{}), 300);
//                                     }
//                                 });
//                             }
//                             playBtn.textContent = "暂停";
//                             statusText.textContent = "播放中";
//                         }, 50);
//                     }
//                 } else {
//                     video.pause();
//                     playBtn.textContent = "播放";
//                     statusText.textContent = "已暂停";
//                 }
//             });
//
//             // 同步时间显示
//             video.addEventListener('timeupdate', () => {
//                 //
//                 // // 更新进度条
//                 // if (CONFIG.totalDuration > 0) {
//                 //     const percent = (video.currentTime / CONFIG.totalDuration) * 100;
//                 //     progressBar.value = percent;
//                 //     timeDisplay.textContent = formatTime(video.currentTime) + " / " + formatTime(CONFIG.totalDuration);
//                 // }
//                 //
//                 // // 【温和的缓冲检测】只在明显超出时提示，不强制暂停，除非真的播不下去了
//                 // if (sourceBuffer && sourceBuffer.buffered.length > 0) {
//                 //     const end = sourceBuffer.buffered.end(sourceBuffer.buffered.length - 1);
//                 //     // 只有当播放头超出缓冲区 1 秒以上，且视频还在尝试播放时，才暂停并加载
//                 //     if (!video.paused && video.currentTime > end + 1.0) {
//                 //         log("播放头超出缓冲，暂停并加载下一段");
//                 //         video.pause();
//                 //         playBtn.textContent = "播放";
//                 //         statusText.textContent = "缓冲中...";
//                 //         if (nextByteToLoad < CONFIG.totalFileSize) {
//                 //             loadChunk(nextByteToLoad);
//                 //         }
//                 //     }
//                 // }
//                 if (!sourceBuffer || sourceBuffer.buffered.length === 0) {
//                     if (!video.seeking) {
//                         progressBar.value = video.currentTime;
//                         timeDisplay.textContent = `${formatTime(video.currentTime)} / ${formatTime(CONFIG.estimatedDuration)}`;
//                     }
//                     return;
//                 }
//
//                 const end = sourceBuffer.buffered.end(sourceBuffer.buffered.length - 1);
//                 const start = sourceBuffer.buffered.start(0);
//
//                 // 【修复 1】增加更宽松的容差 (1 秒)，并且确保视频当前确实是“播放”状态
//                 // 如果视频已经是暂停状态，就没必要再 pause() 了
//                 if (!video.paused && video.currentTime > end + 1.0) {
//                     log(`播放头 (${video.currentTime.toFixed(2)}) 超出缓冲末尾 (${end.toFixed(2)})，暂停`, "warn");
//                     video.pause();
//                     statusText.textContent = "等待缓冲...";
//                     playBtn.textContent = "播放";
//
//                     if (nextByteToLoad < CONFIG.totalFileSize) {
//                         loadChunk(nextByteToLoad);
//                     }
//                 }
//
//                 // 更新 UI
//                 if (!video.seeking) {
//                     progressBar.value = video.currentTime;
//                     timeDisplay.textContent = `${formatTime(video.currentTime)} / ${formatTime(CONFIG.estimatedDuration)}`;
//                 }
//             });
//
//             // 启动
//             // 注意：实际使用时，请确保先获取到准确的 totalFileSize 和 duration
//             // 这里为了演示，假设 CONFIG 中已有估算值，或者你需要在 initPlayer 中完善元数据获取逻辑
//             initPlayer();

            let vp = contain.querySelector('#videoPlayer')

            setTouchRS(vp);
            this.chooseNodeFileCon = contain;
            return false
        }



        const txtExtensions = [".txt", ".json", ".xml", ".csv", ".md", ".html", ".css", ".js", ".log", ".yaml", ".yml"];
        if (txtExtensions.some(ext => lowerName.endsWith(ext))) {
            let frt = fetch("http://" + address + ":" + port + this.urlRight, {
                method: 'post',
                body: param
            })
            addListProcess(ele, param)
            frt.then((Response) => {
                if (Response.ok) {
                    let headers = Response.headers;
                    // 获取固定字段，例如Content-Type
                    let base64EncodedStr = headers.get('filename');
                    filename = decodeBase64ToUtf8(base64EncodedStr)
                    return Response.text()
                } else {
                    throw new Error('Network response was not ok');
                }
            }).catch((e) => {
                console.log(e)
                this.chooseNodeFileCon.parentNode.removeChild(this.chooseNodeFileCon);
                // this.chooseNodepath.pop()
            }).then(async text => {
                removeListProcess(ele)
                cancelTouchRS()
                cancelMouseRS()
                // viewLoadcl()
                this.scrollPos = getScrollPositionRelativeToParent(ele.parentElement)
                this.element.style.display = 'none'
                if (e instanceof Event) {
                    if (this.nodes[this.nodes.length - 1] !== this.element) {
                        this.nodes.push(this.element)
                    }
                    this.element.style.display = 'none'
                    // this.chooseNodeFile=this.element;
                } else {
                    // this.element
                    // this.nodes[this.nodes.length-1]=this.element
                    clearEleChildrenClass(this.ele, 'view')
                    this.chooseNodeFile = null;
                }


                let contain = document.createElement('div')
                contain.setAttribute('class', 'view')
                if (filename !== null && filename !== undefined) {
                    // contain.textContent = atob(filename);
                    contain.textContent = filename;
                }

                console.log("合法");
                let pic = document.createElement('text')
                // pic.setAttribute('class', 'text')
                pic.textContent =text

                contain.addEventListener('click', this.fileToParent.bind(this));
                contain.appendChild(pic);

                this.ele.appendChild(contain)
                this.chooseNodeFileCon = contain;
            }).catch(e => {
                console.log(e)
                // this.dirToParent();
                clearEleChildrenClass(this.ele, 'view')
                // this.element.style.display = null
            })
            return false
        }


        let frt = fetch("http://" + address + ":" + port + this.urlRight, {
            method: 'post',
            body: param
        })
        addListProcess(ele, param)
        frt.then((Response) => {
            if (Response.ok) {
                let headers = Response.headers;
                // 获取固定字段，例如Content-Type
                let base64EncodedStr = headers.get('filename');
                filename = decodeBase64ToUtf8(base64EncodedStr)
                return Response.blob()
            } else {
                throw new Error('Network response was not ok');
            }
        }).catch((e) => {
            console.log(e)
            this.chooseNodeFileCon.parentNode.removeChild(this.chooseNodeFileCon);
            // this.chooseNodepath.pop()
        }).then(async blob => {
            removeListProcess(ele)
            cancelTouchRS()
            cancelMouseRS()
            // viewLoadcl()
            this.scrollPos = getScrollPositionRelativeToParent(ele.parentElement)
            this.element.style.display = 'none'
            if (e instanceof Event) {
                if (this.nodes[this.nodes.length - 1] !== this.element) {
                    this.nodes.push(this.element)
                }
                this.element.style.display = 'none'
                // this.chooseNodeFile=this.element;
            } else {
                clearEleChildrenClass(this.ele, 'view')
                this.chooseNodeFile = null;
            }

            let contain = document.createElement('div')
            contain.setAttribute('class', 'view')
            if (filename !== null && filename !== undefined) {
                // contain.textContent = atob(filename);
                contain.textContent = filename;
            }

            console.log("合法");
            let pic = document.createElement('div')
            pic.setAttribute('class', 'pic')

            contain.addEventListener('click', this.fileToParent.bind(this));


            pic = document.createElement('img')
            // pic.dataset.s = path;
            pic.addEventListener('wheel', this.tranBig.bind(pic))
            pic.dataset.path = path
            pic.addEventListener('touchstart', setTouchLongTimeShow)
            pic.addEventListener('mousedown', setMouseLongTimeShow)
            // pic.style.imageRendering = '-webkit-optimize-contrast';
            // pic.style.imageRendering = 'crisp-edges';
            pic.style.minWidth = '120px';
            pic.style.minHeight = '120px';
            contain.appendChild(pic);

            this.ele.appendChild(contain)
            setTouchRS(pic);
            setMouseRS(pic);
            this.chooseNodeFileCon = contain;
        }).catch(e => {
            console.log(e)
            // this.dirToParent();
            clearEleChildrenClass(this.ele, 'view')
            // this.element.style.display = null
        })
        backFun=this.fileToParent.bind(this)
        return false
    };

    // tranBig(e) {
    //     let v = -e.wheelDelta;
    //     let i = parseFloat(this.dataset.s)
    //     if (isNaN(i)||i===null){
    //         i=1;
    //     }
    //     if (v > 0) {
    //         i = i + 0.1
    //         this.style.transform = `scale(${i})`
    //     } else {
    //         if (i>=0.1){
    //             i = i - 0.1
    //         }
    //         this.style.transform = `scale(${i})`
    //     }
    //     this.dataset.s = i
    // }
    // tranBig(e) {
    //     e.preventDefault();
    //
    //     // 获取当前变换矩阵
    //     const style = window.getComputedStyle(this);
    //     const matrix = new DOMMatrix(style.transform);
    //
    //     // 解析当前状态（自动处理初始状态）
    //     const currentScale = matrix.m11; // X轴缩放比例
    //     const currentTx = matrix.m41;   // X轴平移量
    //     const currentTy = matrix.m42;   // Y轴平移量
    //
    //     // 计算鼠标相对元素的位置
    //     const rect = this.getBoundingClientRect();
    //     const mouseX = e.clientX - rect.left;
    //     const mouseY = e.clientY - rect.top;
    //
    //     // 转换到元素原始坐标系
    //     const originX = (mouseX - currentTx) / currentScale;
    //     const originY = (mouseY - currentTy) / currentScale;
    //
    //     // 计算新缩放比例
    //     const delta = e.wheelDelta > 0 ? 0.1 : -0.1;
    //     const newScale = Math.max(0.1, currentScale + delta);
    //
    //     // 计算新平移量（保持光标位置）
    //     const newTx = mouseX - originX * newScale;
    //     const newTy = mouseY - originY * newScale;
    //
    //     // 应用变换（自动处理初始无变换状态）
    //     this.style.transform = `translate(${newTx}px, ${newTy}px) scale(${newScale})`;
    // }
    tranBig(e) {
        e.preventDefault();

        // 1. 强制 transform-origin 为左上角 (必须，否则数学模型不成立)
        if (this.style.transformOrigin !== '0px 0px') {
            this.style.transformOrigin = '0 0';
        }

        // 2. 获取当前变换矩阵
        const style = window.getComputedStyle(this);
        const matrix = new DOMMatrix(style.transform);

        const currentScale = matrix.m11;
        const currentTx = matrix.m41;
        const currentTy = matrix.m42;

        // 3. 【补全此处】获取元素当前的视口边界矩形
        const rect = this.getBoundingClientRect();

        // 4. 计算鼠标相对于元素“变换后”左上角的偏移量
        const xInBox = e.clientX - rect.left;
        const yInBox = e.clientY - rect.top;

        // 5. 反推鼠标指向的“原始内容坐标”
        // 原理：xInBox = originX * currentScale  =>  originX = xInBox / currentScale
        // 这种直接除法比 matrix.inverse() 更稳定，避免了坐标系定义的陷阱
        const originX = xInBox / currentScale;
        const originY = yInBox / currentScale;

        // 6. 计算新缩放比例
        let delta = 0;
        if (e.wheelDelta) {
            delta = e.wheelDelta > 0 ? 0.1 : -0.1;
        } else if (e.deltaY) {
            delta = e.deltaY < 0 ? 0.1 : -0.1;
        }

        const newScale = Math.max(0.1, Math.min(10, currentScale + delta));

        // 7. 计算新平移量 (补偿缩放带来的位移，保持鼠标指向点不动)
        // 公式：newTx = currentTx + originX * (currentScale - newScale)
        const newTx = currentTx + originX * (currentScale - newScale);
        const newTy = currentTy + originY * (currentScale - newScale);

        // 8. 应用变换
        this.style.transform = `translate(${newTx}px, ${newTy}px) scale(${newScale})`;
    }
    closeCHV() {
        clearEleChildren(this.ele)
        this.ele.style = null
        document.getElementById('listcontainer').style.width=null
        // document.getElementById('listcontainer').style.width='200vw'
        // this.ele.style.height = ''
    }
}

function addListProcess(ele,param) {
    let processb=document.createElement('d');
    processb.setAttribute('class','processb')
    let processCon=document.createElement('d');
    processCon.setAttribute('class','processCon')
    let processv=document.createElement('d');
    processv.setAttribute('class','processv')
    //含边框
    let w=ele.parentNode.offsetWidth
    let h=ele.parentNode.offsetHeight

    processCon.style.width=w-4+'px'
    processCon.style.height=h+'px'
    //不含边框
    //ele.parentNode.clientWidth
    // window.getComputedStyle(ele.parentNode).width

    processCon.appendChild(processv)
    processb.appendChild(processCon)
    ele.appendChild(processb)

    let fun=function (){
        fetch("http://" + address + ":" + port + "/map/userShow/getProcess", {
            method: 'post',
            body: param
        }).then(
            response=>response.json()
        ).then(json=>{
            if (processv.parentNode == null){
                return false
            }
            let e=json['e'];
            let pr=json['pr'];
            if (e==null || e===400){
                removeListProcess(ele)
                return
            }else {
                console.log('processv  '+pr)
                processv.style.width=pr+"%"
            }
            setTimeout(()=>{
                fun()
            },500)
        }).catch(e=>{
            console.log(e)
        })}
    setTimeout(()=>{
        fun()
    },500)


    // let intervalId = setInterval(() => {
    //     fetch("http://" + address + ":" + port + "/map/userShow/getProcess", {
    //         method: 'post',
    //         body: param
    //     }).then(
    //         response=>response.json()
    //     ).then(json=>{
    //         let e=json['e'];
    //         let pr=json['pr'];
    //         if (e===null){
    //             return
    //         }else {
    //             processv.style.width=parseInt(pr)/100+"%"
    //         }
    //         if (processv==null|| processv.parentNode==null){
    //             clearInterval(intervalId)
    //         }
    //         if (e===400){
    //             clearInterval(intervalId)
    //         }
    //
    //     })
    // }, 1000);
    // console.log('intervalId: '+intervalId)
    //
    // ele.dataset.prc=intervalId
}
function removeListProcess(ele) {
   // clearInterval(parseInt(ele.dataset.prc))
   //  console.log('removeListProcess: '+parseInt(ele.dataset.prc))
    console.log('removeListProcess: ')
    let eles = ele.childNodes
    for (let el of eles) {
        if (isElement(el)){
            if (el.tagName.toLowerCase() === 'd') {
                ele.removeChild(el)
            }
        }
    }
}

function setTouchLongTimeShow(e) {
    let eleImg=e.target
    let path=eleImg.dataset.path
    // let timer= setTimeout(function (){
    //     let show = eleImg.parentNode
    //     let so=show.querySelector('.showC')
    //     if (so==null){
    //         let showC=document.createElement('div')
    //         showC.setAttribute('class','showC')
    //         let showCO=document.createElement('div')
    //         showCO.setAttribute('class','showCO')
    //         let but=document.createElement('div')
    //         but.setAttribute('class','listbutton')
    //         but.textContent="下载"
    //         but.dataset.path=path
    //         but.addEventListener("click",downFile)
    //
    //         show.appendChild(showC)
    //         showC.appendChild(showCO)
    //         showCO.appendChild(but)
    //         eleImg.onclick=function (){
    //             this.parentNode.removeChild(showC)
    //         }
    //         eleImg.addEventListener('touchend',function (){
    //             this.parentNode.removeChild(showC)
    //         })
    //     }
    // }, 1400);
    let timestar=new Date().getTime()
    let rem=function (event) {
        // clearTimeout(timer)
        if (new Date().getTime()-timestar>1100 && !touchMoved){
            let show = eleImg.parentNode
            let so=show.querySelector('.showC')
            if (so==null){
                let showC=document.createElement('div')
                showC.setAttribute('class','showC')
                let showCO=document.createElement('div')
                showCO.setAttribute('class','showCO')
                let but=document.createElement('div')
                but.setAttribute('class','listbutton')
                but.textContent="下载"
                but.dataset.path=path

                but.addEventListener("click",downFile)

                let save=document.createElement('div')
                save.setAttribute('class','listbutton')
                save.textContent="保存"
                save.dataset.path=path
                save.addEventListener("click",saveFile)

                show.appendChild(showC)
                showC.appendChild(showCO)
                showCO.appendChild(save)
                showCO.appendChild(but)
                eleImg.onclick=function (){
                    eleImg.parentNode.removeChild(showC)
                }
                eleImg.addEventListener('touchend',function (){
                    eleImg.parentNode.removeChild(showC)
                })
            }
            timestar=new Date().getTime()
        }
        touchMoved=false;
        eleImg.removeEventListener("touchend",rem)
        return false
    }
    eleImg.addEventListener("touchend",rem)
}
function setMouseLongTimeShow(e) {
    let eleImg=e.target
    let path=eleImg.dataset.path
    let timestar=new Date().getTime()
    let rem=function (event) {
        event.stopPropagation(); // 阻止事件冒泡
        event.preventDefault()

        // clearTimeout(timer)
        if (new Date().getTime()-timestar>1100  && unmove){
            let show = eleImg.parentNode
            let so=show.querySelector('.showC')
            if (so==null){
                let showC=document.createElement('div')
                showC.setAttribute('class','showC')
                let showCO=document.createElement('div')
                showCO.setAttribute('class','showCO')
                let but=document.createElement('div')
                but.setAttribute('class','listbutton')
                but.textContent="下载"
                but.dataset.path=path

                but.addEventListener("click",downFile)

                let save=document.createElement('div')
                save.setAttribute('class','listbutton')
                save.textContent="保存"
                save.dataset.path=path
                save.addEventListener("click",saveFile)

                show.appendChild(showC)
                showC.appendChild(showCO)
                showCO.appendChild(save)
                showCO.appendChild(but)
                // eleImg.addEventListener('touchend',function (e){
                //     e.stopPropagation(); // 阻止事件冒泡
                //     e.preventDefault()
                //     eleImg.parentNode.removeChild(showC)
                // })
                eleImg.addEventListener('mouseup',function (e){
                    e.stopPropagation(); // 阻止事件冒泡
                    e.stopImmediatePropagation(); // 阻止所有后续事件（包括捕获阶段）
                    eleImg.parentNode.removeChild(showC)
                })
            }
            timestar=new Date().getTime()
        }
        eleImg.removeEventListener("mouseup",rem)
        return false
    }
    eleImg.addEventListener("mouseup",rem)
}
function downFile(e) {
    let path=e.target.dataset.path
    let filename;
    fetch("http://" + address + ":" + port + "/map/userShow/downFile?" + path)
        .then(response => {
            let headers = response.headers;
            // 获取固定字段，例如Content-Type
            let base64EncodedStr = headers.get('filename');
            filename=decodeBase64ToUtf8(base64EncodedStr)
            return response.blob()})
        .then(blob=>{
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = filename; // 设置文件名
            document.body.appendChild(link); // 临时添加到 DOM（某些浏览器需要）
            link.click();
            document.body.removeChild(link); // 移除元素
            URL.revokeObjectURL(link.href); // 释放内存
        clearEleChildren(e.target.parentNode.parentNode)
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
    }).catch(error => {
            console.error('There has been a problem with your fetch operation:', error);
    });
}

function saveFile(e) {
    let path=e.target.dataset.path
    fetch("http://" + address + ":" + port + "/map/userShow/saveFile?" + path) .then(response => {
        clearEleChildren(e.target.parentNode.parentNode)
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
    }).catch(error => {
        console.error('There has been a problem with your fetch operation:', error);
    });
}

let touchMoved=false;
function setTouchRS(eleImg) {
    let currentDistance = 0
    // const getDistance = (start, stop) => Math.hypot(stop.x - start.x, stop.y - start.y)
    let posStart= {
        x: 0, y: 0
    };
    let tranStart=[]
    let lastDistance = 0

    function getDistance(p1, p2) {
        const dx = p2.x - p1.x;
        const dy = p2.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

// 计算两点中点
    function getMidpoint(p1, p2) {
        return {
            x: (p1.x + p2.x) / 2,
            y: (p1.y + p2.y) / 2
        };
    }
    let moveFun=function (event) {
        event.stopPropagation()
        event.preventDefault()
        const touches = event.touches
        const events = touches[0]
        const events2 = touches[1]
        if (events2) {
            resize(eleImg,
                {x: events.pageX, y: events.pageY},
                {x: events2.pageX, y: events2.pageY})
            touchMoved=true
        }else {
            let dx=events.pageX-posStart.x
            let dy=events.pageY-posStart.y
            if(Math.abs(dx)+Math.abs(dy)>4){
                touchMoved=true
            }
            toPos(eleImg,events)
            let maxEle=getMaxEle()
            let ps={
                left:true ,
                top:true ,
                right:true,
                bottom:true
            }
            let mfun
            if (maxEle.id==='cloudeOpertor'){
                ps=isElementOutOfBounds(document.getElementById('showPib'),eleImg)
                // mfun=openCloudeFile.bind(selectFileEle)
                mfun=function (e) {

                }
            }
            if (maxEle.id==='content-b'){
                ps=isElementOutOfBounds(document.querySelector('#showFile .view'),eleImg)
                mfun=userShowFile.rightchoose.bind(userShowFile)
            }

            let run=false
            if ((!ps.left)||(!ps.top) ){
                while (selectFileEle.previousElementSibling!=null){

                    let f=selectFileEle.previousElementSibling.querySelector('.path')
                    if(f!=null){
                        let ne = true
                        let bf= selectFileEle
                        while (ne && f!=null){
                            selectFileEle=selectFileEle.previousElementSibling
                            f=selectFileEle.querySelector('.path')
                        }
                        if (selectFileEle.previousElementSibling.getAttribute('filename')!==null||
                            selectFileEle.previousElementSibling.querySelector('.file') !== null){
                            selectFileEle=selectFileEle.previousElementSibling
                        }else {
                            selectFileEle = bf
                            break
                        }
                    }
                    if (selectFileEle.previousElementSibling.getAttribute('filename')!==null||
                        selectFileEle.previousElementSibling.querySelector('.file') !== null
                    ){
                        selectFileEle=selectFileEle.previousElementSibling
                        break
                    }else {
                        break
                    }
                    // selectFileEle=selectFileEle.previousElementSibling
                }
                run=true
            }else {
                if ((!ps.right)||(!ps.bottom) ){
                    while (selectFileEle.nextElementSibling!=null){
                        let f=selectFileEle.nextElementSibling.querySelector('.path')
                        if(f!=null){
                            let ne = true
                            let bf= selectFileEle
                            while (ne && f!=null){
                                selectFileEle=selectFileEle.nextElementSibling
                                f=selectFileEle.querySelector('.path')
                            }
                            if (selectFileEle.nextElementSibling.getAttribute('filename')!==null||
                                selectFileEle.nextElementSibling.querySelector('.file') !== null){
                                selectFileEle=selectFileEle.nextElementSibling
                            }else {
                                selectFileEle = bf
                                break
                            }
                        }
                        if (selectFileEle.nextElementSibling.getAttribute('filename')!==null||
                            selectFileEle.nextElementSibling.querySelector('.file') !== null){
                            selectFileEle=selectFileEle.nextElementSibling
                            break
                        }else {
                            break
                        }
                        // selectFileEle=selectFileEle.nextElementSibling
                    }
                    run=true
                }
            }
            if(run){
                if (maxEle.id==='cloudeOpertor'){
                    mfun=openCloudeFile.bind(selectFileEle)
                    mfun()
                    eleImg.removeEventListener('touchmove',moveFun)
                    return false
                }

                if (maxEle.id==='content-b'){
                    mfun(selectFileEle)
                    eleImg.removeEventListener('touchmove',moveFun)
                    return false
                }
            }
        }
    }
    eleImg.addEventListener('touchmove',moveFun , {passive: false})
    eleImg.addEventListener('touchstart', function (event) {
        event.stopPropagation()
        event.preventDefault()
        const touches = event.touches
        const events = touches[0]
        const events2 = touches[1]
        if (events2) {
            const p1 = { x: touches[0].pageX, y: touches[0].pageY };
            const p2 = { x: touches[1].pageX, y: touches[1].pageY };
            lastDistance = getDistance(p1, p2);
        }else {
            posStart.x=events.pageX;
            posStart.y=events.pageY;
            let tr=eleImg.style.translate
            let numbers
            if (tr!==undefined){
                numbers = tr.match(/[+-]?\d+(\.\d+)?/g);
            }
            if (numbers===null){
                numbers=[]
                numbers[0]=0.0
                numbers[1]=0.0
            }else {
                numbers[0]=parseFloat(numbers[0])
                if (isNaN(numbers[0])||numbers[0]===null){
                    numbers[0]=0.0
                }
                numbers[1]=parseFloat(numbers[1])
                if (isNaN(numbers[1])||numbers[1]===null){
                    numbers[1]=0.0
                }
            }
            posStart.x += numbers[0]
            posStart.y += numbers[1]
            tranStart=numbers;
        }
    }
    // , {passive: false}
    )

    let touchM = function (event) {
        event.preventDefault()
        const touches = event.touches
        const events = touches[0]
        const events2 = touches[1]
        if (events2) resize(eleImg,
            {x: events.pageX, y: events.pageY},
            {x: events2.pageX, y: events2.pageY})
    }
    let cl = function () {
        document.removeEventListener('touchmove', setTouchRS.prototype.tm)
    }

    document.addEventListener('touchmove', touchM, {passive: false})
    setTouchRS.prototype.tm = touchM

    setTouchRS.prototype.cl = cl

    // 【重要】需要在外部维护这个变量，用于记录上一帧的双指距离
// 建议在 touchstart 时初始化为 0 或当前距离

    function resize(dom, start, stop) {
        // 1. 计算当前双指距离和中点（视口坐标）
        const currentDistance = getDistance(start, stop);
        const midpoint = getMidpoint(start, stop);

        // 如果距离太小或没有变化，忽略（防止除以零或抖动）
        if (currentDistance < 10 || Math.abs(currentDistance - lastDistance) < 1) {
            lastDistance = currentDistance;
            return;
        }

        // 2. 获取当前变换状态
        const style = window.getComputedStyle(dom);
        // 强制 origin 为 0 0，保证矩阵逻辑与 tranBig 一致
        if (dom.style.transformOrigin !== '0px 0px') {
            dom.style.transformOrigin = '0 0';
        }

        const matrix = new DOMMatrix(style.transform);
        const currentScale = matrix.m11;
        const currentTx = matrix.m41;
        const currentTy = matrix.m42;

        // 3. 计算新的缩放比例
        // 逻辑：新缩放 = 旧缩放 * (当前距离 / 上次距离)
        // 这样缩放是线性的、平滑的，符合手指拉伸的物理直觉
        const scaleRatio = currentDistance / lastDistance;
        let newScale = currentScale * scaleRatio;

        // 限制缩放范围 (可选，根据需求调整)
        newScale = Math.max(0.1, Math.min(10, newScale));

        // 4. 计算缩放中心点在“原始内容坐标系”中的位置 (originX, originY)
        // 这一步逻辑与 tranBig 完全一致，确保行为统一

        // 获取当前元素的视口边界
        const rect = dom.getBoundingClientRect();

        // 计算中点相对于元素“变换后”左上角的偏移
        const xInBox = midpoint.x - rect.left;
        const yInBox = midpoint.y - rect.top;

        // 反推原始坐标：Origin = Offset / Scale
        const originX = xInBox / currentScale;
        const originY = yInBox / currentScale;

        // 5. 计算新的平移量 (补偿缩放)
        // 公式推导：为了保持 midpoint 在视口中的位置不变
        // newTx = currentTx + originX * (currentScale - newScale)
        const newTx = currentTx + originX * (currentScale - newScale);
        const newTy = currentTy + originY * (currentScale - newScale);

        // 6. 应用变换
        dom.style.transform = `translate(${newTx}px, ${newTy}px) scale(${newScale})`;

        // 7. 更新 lastDistance 供下一帧使用
        lastDistance = currentDistance;
    }
    // var num4 = str2.match(/[^\d\.]/g);
    // console.log(num4); // ["+", "*", "-", "/", "+"]
    // function resize(dom, start, stop) {
    //     const currentVal = getDistance(start, stop)
    //     if (currentDistance < currentVal) {
    //         let str=dom.style.transform
    //         const translateRegex = /translate\(([^)]+)\)/;
    //         // const scaleRegex = /scale$[^)]+$/;
    //         const scaleRegex = /scale\(([^)]+)\)/;
    //         // str=str.match(translateRegex);
    //         // 匹配translate中的数值并替换
    //         // let modifiedTransformString = str.replace(/scale$([^)]+)$/, (match, p1) => {
    //         //     // 提取translate中的数字
    //         //     let numbers = p1.match(/-?\d*\.?\d+/g).map(Number);
    //         //     // 将数字除以10
    //         //     // numbers = numbers.map(num => num / 10);
    //         //     // 返回新的translate部分
    //         //     if (numbers===null){
    //         //         numbers=1;
    //         //     }else {
    //         //         if (numbers.length>1){
    //         //             let numStr = numbers[0] + '.' + numbers[1];
    //         //             // 使用parseFloat函数将字符串解析为浮点数
    //         //             numbers = parseFloat(numStr);
    //         //         }else {
    //         //             numbers=parseFloat(numbers[0])
    //         //         }
    //         //     }
    //         //     dom.style.width = dom.offsetWidth * 1.1 + 'px'
    //         //     numbers = numbers + 0.1
    //         //     return `scale(${numbers[0]}px, ${numbers[1]}px)`;
    //         // });
    //         // if (str ===''){
    //         //
    //         // }
    //         // 处理translate部分
    //         let scaleMatch = str.match(scaleRegex);
    //         if (scaleMatch) {
    //             let numbers = scaleMatch[1].match(/-?\d*\.?\d+/g).map(function(num) {
    //                 let numbers=num
    //                     // 返回新的translate部分
    //                     if (numbers===null){
    //                         numbers=1;
    //                     }else {
    //                         // if (numbers.length>1){
    //                         //     let numStr = numbers[0] + '.' + numbers[1];
    //                         //     // 使用parseFloat函数将字符串解析为浮点数
    //                         //     numbers = parseFloat(numStr);
    //                         // }else {
    //                         //     numbers=parseFloat(numbers[0])
    //                         // }
    //                         numbers=parseFloat(numbers)
    //                     }
    //                     numbers = numbers + 0.1
    //                 return numbers;
    //             });
    //             let newTranslatePart="scale(1.00, 1.00)"
    //             if (numbers.length>1){
    //                 newTranslatePart="scale(" + numbers[0].toFixed(2) + ", " + numbers[1].toFixed(2) + ")";
    //             }else {
    //                 newTranslatePart = "scale(" + numbers[0].toFixed(2) + ")";
    //             }
    //
    //             dom.style.transform = str.replace(scaleRegex, newTranslatePart);
    //         } else {
    //             // 如果没有找到translate，则添加translate(0px, 0px)
    //             let newTranslatePart = "scale(1.00, 1.00)";
    //             if (translateRegex.test(str)) {
    //                 // 如果存在scale，将translate插入到scale之前
    //                 str = str.replace(scaleRegex, newTranslatePart + " " + function() { return arguments[0]; });
    //             } else {
    //                 // 如果不存在其他变换，直接在末尾添加translate
    //                 str += " " + newTranslatePart;
    //             }
    //             dom.style.transform = str
    //         }
    //
    //         // let numbers = str.match(/\d+(\.\d+)?/g);
    //         // if (numbers===null){
    //         //     numbers=1;
    //         // }else {
    //         //     if (numbers.length>1){
    //         //         let numStr = numbers[0] + '.' + numbers[1];
    //         //         // 使用parseFloat函数将字符串解析为浮点数
    //         //         numbers = parseFloat(numStr);
    //         //     }else {
    //         //         numbers=parseFloat(numbers[0])
    //         //     }
    //         // }
    //         // dom.style.width = dom.offsetWidth * 1.1 + 'px'
    //         // numbers = numbers + 0.1
    //         // dom.style.transform = `scale(${numbers})`
    //     } else {
    //         // dom.style.width = dom.offsetWidth * 0.9 + 'px'
    //         let str=dom.style.transform
    //         const scaleRegex = /scale\(([^)]+)\)/;
    //         const translateRegex = /translate\(([^)]+)\)/;
    //         let scaleMatch=str.match(scaleRegex);
    //         let numbers
    //         if (scaleMatch!==null){
    //             numbers = scaleMatch[1].match(/-?\d*\.?\d+/g);
    //             if (numbers===null || numbers===undefined){
    //                 numbers=1;
    //             }else {
    //                 if (numbers.length>1){
    //                     // let numStr = numbers[0] + '.' + numbers[1];
    //                     numbers[0] = parseFloat(numbers[0]);
    //                     numbers[1] = parseFloat(numbers[1]);
    //                     if (numbers[0]>0.3){
    //                         numbers[0] = numbers[0] - 0.1
    //                     }
    //                     if (numbers[1]>0.3){
    //                         numbers[1] = numbers[1] - 0.1
    //                     }
    //
    //                     let newTranslatePart = "scale(" +
    //                         numbers[0].toFixed(2) + ", " + numbers[1].toFixed(2) + ")";
    //                     dom.style.transform = str.replace(scaleRegex, newTranslatePart);
    //
    //                 }else {
    //                     numbers=parseFloat(numbers[0])
    //                     if (numbers>0.3){
    //                         numbers = numbers - 0.1
    //                         let newTranslatePart = "scale(" + numbers.toFixed(2) + ")";
    //                         dom.style.transform = str.replace(scaleRegex, newTranslatePart);
    //                     }
    //                 }
    //                 if (isNaN(numbers)){
    //                     numbers=0.9;
    //                     let newTranslatePart = "scale(" + numbers.toFixed(2) + ")";
    //                     dom.style.transform = str.replace(scaleRegex, newTranslatePart);
    //                 }
    //             }
    //             // // dom.style.width = dom.offsetWidth * 1.1 + 'px'
    //             // if (numbers>0.3){
    //             //     numbers = numbers - 0.1
    //             //     let newTranslatePart = "scale(" +
    //             //         numbers[0].toFixed(2) + ", " + numbers[1].toFixed(2) + ")";
    //             //     dom.style.transform = str.replace(translateRegex, newTranslatePart);
    //             // }
    //         }else {
    //             let newScalePart = "scale(1.00, 1.00)";
    //             if (translateRegex.test(str)) {
    //                 // 如果存在scale，将translate插入到scale之前
    //                 str = str.replace(scaleRegex, newScalePart + " " + function() { return arguments[0]; });
    //             } else {
    //                 // 如果不存在其他变换，直接在末尾添加translate
    //                 str += " " + newScalePart;
    //             }
    //             dom.style.transform = str
    //         }
    //     }
    //     currentDistance = currentVal
    // }
    function toPos(dom, event) {
        let dx=event.pageX-posStart.x
        let dy=event.pageY-posStart.y
        // dx = event.x
        let str=dom.style.transform

        // const translateRegex = /translate$([^)]+)$/;
        const translateRegex=/translate\(([^)]+)\)/

        const scaleRegex = /scale$[^)]+$/;
        let translateMatch = str.match(translateRegex);
        if (translateMatch) {
            let numbers = translateMatch[1].match(/-?\d*\.?\d+/g).map(function(num) {
                let numberss=num
                // 返回新的translate部分
                if (numberss===null){
                    numberss=1;
                }else {
                    numberss=parseFloat(numberss)
                }
                return numberss;
            });
            numbers[0]+=dx
            numbers[1]+=dy
            let newTranslatePart = "translate(" + numbers[0].toFixed(2) + "px, " + numbers[1].toFixed(2) + "px)";
            dom.style.transform = str.replace(translateRegex, newTranslatePart);
            posStart.x = event.pageX
            posStart.y = event.pageY
        } else {
            // 如果没有找到translate，则添加translate(0px, 0px)
            let newTranslatePart = "translate(0.00px, 0.00px)";
            if (scaleRegex.test(str)) {
                // 如果存在scale，将translate插入到scale之前
                str = str.replace(scaleRegex, newTranslatePart + " " + function() { return arguments[0]; });
            } else {
                // 如果不存在其他变换，直接在末尾添加translate
                str += " " + newTranslatePart;
            }
            dom.style.transform = str

        }
        // dom.style.transform = str
        // let style = window.getComputedStyle(dom);
        // let matrix = new WebKitCSSMatrix(style.transform);
        // //matrix.m41
        // let tr=style.translate
        // let scale = style.getPropertyValue('transform');
        // let scaleY = style.getPropertyValue('translate');

        // let v=`${tranStart[0]+dx}px  ${tranStart[1]+dy}px`
        // dom.style.translate= v
    }
}

function cancelTouchRS() {
    try {
        setTouchRS.prototype.cl();
    }catch (e) {
        console.log(e)
    }
}



function setMouseRS(eleImg) {
    let posStart= {x: 0, y: 0};
    let tranStart = []
    let mouseMoved=false;
    let moveFun=function (event) {
        if(!mouseMoved)return
        event.stopPropagation()
        event.preventDefault()
        toPos(eleImg,event)
        let maxEle=getMaxEle()
        let ps={
            left:true ,
            top:true ,
            right:true,
            bottom:true
        }
        let mfun
        // if (maxEle.id==='cloudeOpertor'){
        //     mfun=function (e) {
        //
        //     }
        // }
        if (maxEle.id==='content-b'){
            ps=isElementOutOfBounds(document.querySelector('#showFile .view'),eleImg)
            mfun=userShowFile.rightchoose.bind(userShowFile)
        }

        if ((!ps.left)||(!ps.top) ){
            while (selectFileEle.previousElementSibling!=null){

                let f=selectFileEle.previousElementSibling.querySelector('.path')
                if(f!=null){
                    let ne = true
                    let bf= selectFileEle
                    while (ne && f!=null){
                        selectFileEle=selectFileEle.previousElementSibling
                        f=selectFileEle.querySelector('.path')
                    }
                    if (selectFileEle.previousElementSibling.getAttribute('filename')!==null||
                        selectFileEle.previousElementSibling.querySelector('.file') !== null){
                        selectFileEle=selectFileEle.previousElementSibling
                    }else {
                        selectFileEle = bf
                        break
                    }
                }
                if (selectFileEle.previousElementSibling.getAttribute('filename')!==null||
                    selectFileEle.previousElementSibling.querySelector('.file') !== null
                ){
                    selectFileEle=selectFileEle.previousElementSibling
                    break
                }else {
                    break
                }
                // selectFileEle=selectFileEle.previousElementSibling
            }
            mfun(selectFileEle)
            eleImg.removeEventListener('mousemove',moveFun)
            return false
        }else {
            if ((!ps.right)||(!ps.bottom) ){
                while (selectFileEle.nextElementSibling!=null){
                    let f=selectFileEle.nextElementSibling.querySelector('.path')
                    if(f!=null){
                        let ne = true
                        let bf= selectFileEle
                        while (ne && f!=null){
                            selectFileEle=selectFileEle.nextElementSibling
                            f=selectFileEle.querySelector('.path')
                        }
                        if (selectFileEle.nextElementSibling.getAttribute('filename')!==null||
                            selectFileEle.nextElementSibling.querySelector('.file') !== null){
                            selectFileEle=selectFileEle.nextElementSibling
                        }else {
                            selectFileEle = bf
                            break
                        }
                    }
                    if (selectFileEle.nextElementSibling.getAttribute('filename')!==null||
                        selectFileEle.nextElementSibling.querySelector('.file') !== null){
                        selectFileEle=selectFileEle.nextElementSibling
                        break
                    }else {
                        break
                    }
                    // selectFileEle=selectFileEle.nextElementSibling
                }
                mfun(selectFileEle)
                eleImg.removeEventListener('mousemove',moveFun)
                return false
            }
        }
        touchMoved=true

    }
    eleImg.addEventListener('mousemove',moveFun , {passive: false})
    eleImg.addEventListener('mousedown', function (event) {
            event.stopPropagation()
            event.preventDefault()
            mouseMoved=true
            posStart.x=event.pageX;
            posStart.y=event.pageY;
            let tr=eleImg.style.translate
            let numbers
            if (tr!==undefined){
                numbers = tr.match(/[+-]?\d+(\.\d+)?/g);
            }
            if (numbers===null){
                numbers=[]
                numbers[0]=0.0
                numbers[1]=0.0
            }else {
                numbers[0]=parseFloat(numbers[0])
                if (isNaN(numbers[0])||numbers[0]===null){
                    numbers[0]=0.0
                }
                numbers[1]=parseFloat(numbers[1])
                if (isNaN(numbers[1])||numbers[1]===null){
                    numbers[1]=0.0
                }
            }
            posStart.x += numbers[0]
            posStart.y += numbers[1]
            tranStart=numbers;

        }
        // , {passive: false}
    )
    function moveover(e) {
        mouseMoved=false
    }
    eleImg.addEventListener('mouseup',moveover , {passive: false})
    eleImg.addEventListener('mouseover',moveover , {passive: false})

    let cl = function () {
        document.removeEventListener('mousemove', setMouseRS.prototype.tm)
    }
    setMouseRS.prototype.cl = cl
    function toPos(dom, event) {
        let dx=event.pageX-posStart.x
        let dy=event.pageY-posStart.y
        let str=dom.style.transform
        const translateRegex=/translate\(([^)]+)\)/
        const scaleRegex = /scale$[^)]+$/;
        let translateMatch = str.match(translateRegex);
        if (translateMatch) {
            let numbers = translateMatch[1].match(/-?\d*\.?\d+/g).map(function(num) {
                let numberss=num
                // 返回新的translate部分
                if (numberss===null){
                    numberss=1;
                }else {
                    numberss=parseFloat(numberss)
                }
                return numberss;
            });
            numbers[0]+=dx
            numbers[1]+=dy
            let newTranslatePart = "translate(" + numbers[0].toFixed(2) + "px, " + numbers[1].toFixed(2) + "px)";
            dom.style.transform = str.replace(translateRegex, newTranslatePart);
            posStart.x = event.pageX
            posStart.y = event.pageY
        } else {
            // 如果没有找到translate，则添加translate(0px, 0px)
            let newTranslatePart = "translate(0.00px, 0.00px)";
            if (scaleRegex.test(str)) {
                // 如果存在scale，将translate插入到scale之前
                str = str.replace(scaleRegex, newTranslatePart + " " + function() { return arguments[0]; });
            } else {
                // 如果不存在其他变换，直接在末尾添加translate
                str += " " + newTranslatePart;
            }
            dom.style.transform = str
        }
    }
}

function cancelMouseRS() {
    try {
        setMouseRS.prototype.cl();
    }catch (e) {
        console.log(e)
    }
}
// document.getElementById("box").style.cssText = "height:20px;width:20px;color:red;";

class FileSelect {
    constructor(
        ele = document.querySelector("#TMView"),
        // selectFunction=optFileB,
        urlRoot = "/map/show/openPathRoot",
        urlPathList = "/map/bitView/vPathList?",
        urlParent = "/map/bitView/openParentP?"
    ) {
        this.ele = ele;
        this.urlRoot = urlRoot;
        this.urlPathList = urlPathList
        this.urlParent = urlParent
        this.selectFunction = null
        this.element = document.createElement('FileSelectView')
        ele.style.position = "fixed";
        ele.style.height = "100vh";
        ele.style.width = "100vw";
        ele.style.maxWidth = "300px";
        ele.style.top = "0";
        ele.style.right = "0";
        ele.style.display = "none";
        this.element.setAttribute('class', 'FileSelectView')
        ele.appendChild(this.element)
        this.chooseNodepath = "";
        this.select = this.element;
    }


    fileMenu(e) {
        let orgin = this.element;
        if (e.target !== this.ele && e.target !== this.ele.parentNode) {
            return
        }
        this.ele.style.display = "flex";
        this.element.style.display = "initial";
        clearEleChildren(orgin);
        let choose = document.createElement('choose');
        choose.setAttribute('class', 'optFile');

        let close = document.createElement('chooseclose');

        close.setAttribute('class', 'closechoose');
        close.innerText = '取消'
        close.addEventListener('click', this.closeCHV.bind(this));
        choose.appendChild(close);
        orgin.appendChild(choose);
        fetch("http://" + address + ":" + port + this.urlRoot, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = document.createElement('con');
            contain.setAttribute('class', 'contain')
            choose.appendChild(contain);
            for (var key in jsondatas) {
                var li = document.createElement("path");
                var ss = jsondatas[key];
                li.setAttribute("data", ss);
                li.dataset.locadpath = jsondatas[key]
                console.log(ss);
                li.setAttribute("data", ss);
                li.dataset.locadpath = jsondatas[key]
                var dv = document.createElement("dv");
                dv.innerText = ss;
                li.appendChild(dv);
                li.addEventListener('click', this.optPathP.bind(this));
                contain.appendChild(li);
            }
        });
    }

    optPathP(e) {
        if (e.target.tagName !== "PATH" && e.target.tagName !== "FILE") {
            return
        }
        let param
        if (this.chooseNodepath === '') {
            param = e.target.dataset.locadpath
        } else {
            param = this.chooseNodepath + "/" + e.target.dataset.locadpath
        }

        this.chooseNodepath = param
        fetch("http://" + address + ":" + port + this.urlPathList + param, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = choose.querySelector('.contain');
            clearEleChildren(contain)
            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            path.innerText = this.chooseNodepath;
            contain.appendChild(path);
            path.addEventListener('click', this.toFileParent.bind(this));

            for (var key in jsondatas) {
                var li
                if (jsondatas[key] === 'f') {
                    li = document.createElement("file");
                    li.setAttribute('class', 'file')
                    li.setAttribute('filename', key)
                    li.addEventListener('click', this.selectFunction)
                } else {
                    li = document.createElement("path");
                    li.setAttribute('class', 'path')
                    li.setAttribute('pathname', key)
                    li.addEventListener('click', this.optPathP.bind(this))
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

    toFileParent(e) {
        fetch("http://" + address + ":" + port + this.urlParent + this.chooseNodepath, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = this.element.querySelector(".optFile");
            let contain = choose.querySelector('.contain');
            clearEleChildren(contain)
            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            this.chooseNodepath = getLastSegment(this.chooseNodepath)
            path.innerText = this.chooseNodepath;
            contain.appendChild(path);
            path.addEventListener('click', this.toFileParent.bind(this));

            for (let key in jsondatas) {
                let li
                if (jsondatas[key] === 'f') {
                    li = document.createElement("file");
                    li.setAttribute('class', 'file')
                    li.setAttribute('filename', key)
                    li.addEventListener('click', optFileB)
                } else {
                    li = document.createElement("path");
                    li.setAttribute('class', 'path')
                    li.setAttribute('pathname', key)
                    li.addEventListener('click', this.optPathP.bind(this))
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

    closeCHV() {
        let node = this.element;
        node.parentNode.removeChild(node);
        // node.innerHTML=''
        // this.ele.style.display="none"
        this.chooseNodepath = '';
    }
}


// class FileView {
//     constructor(
//         but = document.querySelector("#TMView"),
//         ele = document.querySelector("#TMView"),
//         // selectFunction=optFileB,
//         urlRoot = "/map/show/openPathRoot",
//         urlPathList = "/map/show/cPathList1?",
//         urlParent = "/map/bitView/openParentP?"
//     ) {
//         this.ele = ele;
//         this.urlRoot = urlRoot;
//         this.urlPathList = urlPathList
//         this.urlParent = urlParent
//         this.selectFunction = null
//         this.element = document.createElement('FileSelectView')
//         this.element.style.position = "fixed";
//         this.element.style.height = "100%";
//         this.element.style.width = "100%";
//         this.element.style.maxWidth = "300px";
//         this.element.style.top = "0";
//         ele.style.right = "0";
//         ele.style.display = "none";
//         this.element.setAttribute('class', 'FileSelectView')
//         ele.appendChild(this.element)
//         this.chooseNodepath = "";
//         this.dataShowFile = {};
//         this.viewFileArray = [];
//     }
//
//
//     viewFile(element) {
//         // 遍历元素的祖先元素
//         let dragelement = draggableElement
//         let target = dragelement.dataset.target;
//         let absolute = dragelement.dataset.absolute;
//         let abpath = absolute + target
//         if (this.dataShowFile[abpath] !== null || this.dataShowFile[abpath] !== undefined) {
//             this.dataShowFile = {}
//             let obj = {}
//             obj['ab'] = abpath
//             obj['name'] = window.username
//             // let abs=ab.split('/')
//             obj['path'] = target
//             let param = JSON.stringify(obj)
//             fetch("http://" + address + ":" + port + this.urlPathList + param)
//                 .then((Response) => Response.json())
//                 .then((json) => {
//                     console.log(json)
//                     let data = {}
//                     data['name'] = window.username;
//                     let review = []
//                     // let keys = Object.keys(json);
//                     let fileView = document.createElement("div");
//                     fileView.setAttribute('class', 'fileView');
//
//                     let divcontains = document.querySelector("#viewFileCon")
//                     let w = document.documentElement.clientWidth - 30
//                     let ww = w / 100
//                     let wc = (w % 100) / (w / 100)
//                     let del = document.createElement("div");
//                     del.setAttribute('class', 'upParent')
//                     del.addEventListener('click', upbutton)
//                     fileView.appendChild(del)
//                     del.style.width = 100 + wc + 'px'
//                     del.style.height = 100 + wc + 'px'
//                     // let div = document.createElement("div")
//                     let ss;
//                     let divn;
//                     let url = "http://" + address + ":" + port + "/map/ActionCloude/reViewPic?"
//                     let p;
//                     for (let key in json) {
//                         divn = document.createElement("div")
//                         if (json[key] === 'f') {
//                             divn.setAttribute('class', 'file')
//                             divn.setAttribute('filename', key)
//                             divn.addEventListener('click', openCloudeFile)
//
//                             data['file'] = key
//                             if (key.includes('.jpg') || key.includes('.png') || key.includes('.jpeg')) {
//                                 review[review.length] = divn
//                                 // let d = JSON.stringify(data)
//                                 // divn.style.backgroundImage= `url("${url+d}")`
//                                 // let img=document.createElement('img')
//                                 // img.setAttribute('src',url+d)
//                                 // divn.appendChild(img)
//                                 // img.setAttribute('alt',key)
//                             }
//                         } else {
//                             divn.setAttribute('class', 'path')
//                             divn.setAttribute('pathname', key)
//                             divn.addEventListener('click', vieWCloudePathListson)
//                         }
//                         p = document.createElement('div')
//                         p.setAttribute('class', 'fileTxt')
//                         ss = key.split('/')
//                         p.textContent = ss[ss.length - 1]
//                         divn.appendChild(p)
//
//                         // divn.innerText =
//
//                         divn.dataset.abpath = key
//                         fileView.appendChild(divn)
//                     }
//                     divcontains.dataset.ab = abpath
//                     divcontains.dataset.name = window.username
//
//                     divcontains.appendChild(fileView)
//                     viewFileArray[viewFileArray.length] = fileView
//                     dataShowFile[0] = target;
//
//                     // for (let k in viewFileArray) {
//                     //     viewFileArray[k].style.display='none'
//                     // }
//                     viewFileArray[viewFileArray.length - 1].style.display = 'flex'
//                     opertaContainer.style.width = '100vw'
//
//
//                     var elements = document.querySelectorAll('.fileView div');
//                     elements.forEach(function (ele) {
//                         // 获取元素的文本内容并计算其长度
//                         // 设置元素的宽度
//                         ele.style.width = 100 + wc + 'px'
//                         ele.style.height = 100 + wc + 'px'
//                     });
//
//                     review.forEach(function (ele) {
//                         // 获取元素的文本内容并计算其长度
//                         // 设置元素的宽度
//                         data['file'] = ele.getAttribute('filename')
//                         let d = JSON.stringify(data)
//                         let img = document.createElement('img')
//                         img.setAttribute('src', url + d)
//                         ele.appendChild(img)
//                         img.setAttribute('alt', data['file'])
//                         // ele.style.backgroundImage = `url("${url+d}")`
//                         // ele.style.backgroundRepeat = "no-repeat";
//                         // ele.style.backgroundSize = "cover"; // 或者 "contain", "100% 100%", 等
//                         // ele.style.backgroundSize="center"
//                     });
//                 })
//         }
//         // 没有找到符合条件的祖先元素
//         return false;
//     }
//     viewCloudePathListson(e) {
//         let ele = this.parentNode
//         let data = {}
//         data['ab'] = ele.parentNode.dataset.ab;
//         data['name'] = ele.parentNode.dataset.name;
//         let abs = this.dataset.abpath.split('/');
//         data['path'] = abs[abs.length - 1];
//         let param = JSON.stringify(data);
//         loadstart();
//         fetch("http://" + address + ":" + port + "/map/show/cPathList", {
//             method: 'post',
//             body: param
//         })
//             .then((Response) => Response.json())
//             .then((json) => {
//                 json = JSON.parse(json)
//                 console.log(json)
//                 let data = {}
//                 data['name'] = window.username;
//                 let review = []
//                 // let keys = Object.keys(json);
//                 let fileView = document.createElement("div");
//                 fileView.setAttribute('class', 'fileView');
//
//                 let divcontains = document.querySelector("#viewFileCon")
//                 let w = Number(window.getComputedStyle(divcontains).width.replace('px', '')) - 10
//                 let wc = (w % 100) / (w / 100)
//                 let del = document.createElement("div");
//                 del.setAttribute('class', 'upParent')
//                 del.addEventListener('click', upbutton)
//                 fileView.appendChild(del)
//                 del.style.width = 100 + wc + 'px'
//                 del.style.height = 100 + wc + 'px'
//                 // let div = document.createElement("div")
//                 let ss;
//                 let divn;
//                 let url = "http://" + address + ":" + port + "/map/ActionCloude/reViewPic?"
//                 let p;
//                 for (let key in json) {
//                     divn = document.createElement("div")
//                     if (json[key] === 'f') {
//                         divn.setAttribute('class', 'file')
//                         divn.setAttribute('filename', key)
//                         divn.addEventListener('click', openCloudeFile)
//                         data['file'] = key
//                         if (key.includes('.jpg') || key.includes('.png') || key.includes('.jpeg')) {
//                             review[review.length] = divn
//                         }
//                     } else {
//                         divn.setAttribute('class', 'path')
//                         divn.setAttribute('pathname', key)
//                         divn.addEventListener('click', vieWCloudePathListson)
//                     }
//                     p = document.createElement('div')
//                     p.setAttribute('class', 'fileTxt')
//                     ss = key.split('/')
//                     p.textContent = ss[ss.length - 1]
//                     divn.appendChild(p)
//                     divn.dataset.abpath = key
//                     fileView.appendChild(divn)
//                 }
//                 divcontains.appendChild(fileView)
//
//                 for (let k in viewFileArray) {
//                     viewFileArray[k].style.display = 'none'
//                 }
//                 viewFileArray[viewFileArray.length] = fileView
//
//                 var elements = document.querySelectorAll('.fileView div');
//                 elements.forEach(function (ele) {
//                     // 获取元素的文本内容并计算其长度
//                     // 设置元素的宽度
//                     ele.style.width = 100 + wc + 'px'
//                     ele.style.height = 100 + wc + 'px'
//                 });
//
//                 review.forEach(function (ele) {
//                     // 获取元素的文本内容并计算其长度
//                     // 设置元素的宽度
//                     data['file'] = ele.getAttribute('filename')
//                     let d = JSON.stringify(data)
//                     let img = document.createElement('img')
//                     img.setAttribute('src', url + d)
//                     ele.appendChild(img)
//                     img.setAttribute('alt', data['file'])
//                 });
//
//                 loadstart();
//             }).then(() => {
//             loadstop()
//         }).catch(e => loadstop())
//     }
//     upbutton(e) {
//         this.parentNode.parentNode.removeChild(this.parentNode)
//         viewFileArray = viewFileArray.slice(0, -1)
//         if (viewFileArray.length === 0) {
//             opertaContainer.style.width = '388px'
//         } else {
//             viewFileArray[viewFileArray.length - 1].style.display = 'flex'
//         }
//     }
// }
