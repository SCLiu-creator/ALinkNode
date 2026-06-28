
function SelectPathFun(fun) {
    let unfoldState=true
    function showUnfold(e) {
        e.preventDefault();e.stopPropagation();
        if (unfoldState){
            let choose = document.querySelector(".dragContainer .orgin .choosefile");
            choose.style.maxWidth='270px';
            choose.style.width='80vw';
            choose.style.minWidth='160px';
            let trianglecon = document.querySelector(".dragContainer .orgin .triangle-container");
            let triangle1 = document.querySelector(".dragContainer .orgin .triangle-1");
            let triangle2 = document.querySelector(".dragContainer .orgin .triangle-2");
            trianglecon.style.left= '44%';
            triangle1.style.transform='rotate(0deg)';
            triangle2.style.transform='rotate(-270deg)';

            unfoldState=false
        }else {
            let choose = document.querySelector(".dragContainer .orgin .choosefile");
            let triangle1 = document.querySelector(".dragContainer .orgin .triangle-1");
            let triangle2 = document.querySelector(".dragContainer .orgin .triangle-2");
            let trianglecon = document.querySelector(".dragContainer .orgin .triangle-container");
            trianglecon.style.left= '65px';
            choose.style.width='160px';
            triangle1.style.transform='rotate(90deg)';
            triangle2.style.transform='rotate(-180deg)';

            unfoldState=true
        }
    }


    function addfile(where, html) {
        let choose = document.querySelector(".dragContainer .orgin .choosefile");
        if (choose != null) {
            return;
        }

        let orgin = document.querySelector(".dragContainer .orgin");

        choose = document.createElement('choose');
        let close = document.createElement('chooseclose');
        let chooseright = document.createElement('chooseright');
        chooseright.innerText='确认'
        let chooseunfold = document.createElement('div');
        choose.setAttribute('class', 'choosefile');
        close.setAttribute('class', 'closechoose');
        close.innerText='取消'

        chooseunfold.setAttribute('class',"triangle-container")
        let triangle= '  <div class="triangle triangle-1"></div>  \n' + '  <div class="triangle triangle-2"></div>  \n'
        chooseunfold.insertAdjacentHTML('beforeend', triangle)
        chooseunfold.addEventListener('click',showUnfold)

        chooseright.setAttribute('class', 'rightchoose')
        chooseright.addEventListener('click', rightchoose);
        close.addEventListener('click', closechoose);
        chooseright.addEventListener('click', closechoose);
        choose.appendChild(close);
        choose.appendChild(chooseunfold);
        choose.appendChild(chooseright);
        orgin.appendChild(choose);
        fetch("http://" + address + ":" + port + "/map/show/openPathRoot", {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = document.querySelector(".dragContainer .orgin .choosefile");
            let contain = document.createElement('contain');
            contain.setAttribute('class', 'filecontain')
            choose.appendChild(contain);
            for (var jsondata in jsondatas) {

                var li = document.createElement("file");
                console.log(jsondatas);
                // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
                var ss = jsondatas[jsondata];
                li.setAttribute("data", ss);
                li.dataset.locadpath = jsondatas[jsondata]
                var dv = document.createElement("dv");

                dv.innerText = ss;
                let a = document.createElement("a");
                let b = document.createElement("b");
                a.appendChild(dv);
                li.appendChild(a);
                li.appendChild(b);
                li.addEventListener('click', chooseFile);
                b.innerHTML = '<input type="checkbox" id="horns" name="horns" />';
                contain.appendChild(li);
            }
        });
    }

    function chooseFile(e) {
        if (e.target.tagName == 'INPUT') {
            return;
        }
        let ele = e.target;
        while (ele.tagName !== 'FILE') {
            if (ele.parentNode === null) {
                e.target = null;
                return;
            }
            ele = ele.parentNode;
        }
        let parentdoc = document.querySelector(".dragContainer .orgin docmentPath");
        let abpath;
        let param = this.dataset.locadpath
        if (parentdoc == null) {
            abpath = "";
        } else {
            abpath = parentdoc.dataset.abpath
            param = abpath + '/' + param
        }

        fetch("http://" + address + ":" + port + "/map/show/openPath?" + param, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatas => {
            let choose = document.querySelector(".dragContainer .orgin .choosefile");
            let contain = document.querySelector('.dragContainer .orgin .choosefile contain');
            ele.parentNode.innerHTML = '';
            contain.setAttribute('class', 'filecontain')

            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            path.dataset.abpath = param;
            path.innerText = '...';
            contain.appendChild(path);
            path.addEventListener('click', chooseFileParent);

            for (var jsondata in jsondatas) {
                var li = document.createElement("file");
                console.log(jsondatas);
                // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
                var ss = jsondatas[jsondata];
                li.setAttribute("data", ss);
                li.dataset.locadpath = jsondatas[jsondata]
                var dv = document.createElement("dv");

                dv.innerText = ss;
                let a = document.createElement("a");
                let b = document.createElement("b");
                a.appendChild(dv);
                li.appendChild(a);
                li.appendChild(b);
                li.addEventListener('click', chooseFile);
                b.innerHTML = '<input type="checkbox" id="horns" name="horns" />';
                contain.appendChild(li);

            }
        });
    }

    function chooseFileParent(e) {
        if (e.target != this) {
            return;
        }
        let parentdoc = document.querySelector(".dragContainer .orgin docmentPath");
        let abpath;
        if (parentdoc == null) {
            abpath = "";
        } else {
            abpath = parentdoc.dataset.abpath;
        }

        let param = abpath;
        fetch("http://" + address + ":" + port + "/map/show/openParentPath?" + param, {withCredentials: true})
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json);
                return json;
            }).then(jsondatass => {
            let choose = document.querySelector(".dragContainer .orgin .choosefile");
            let contain = document.querySelector('.dragContainer .orgin .choosefile contain');
            contain.innerHTML = "";
            contain.setAttribute('class', 'filecontain')
            choose.appendChild(contain);
            let path = document.createElement('docmentPath');
            let paramstring = param.split('/');
            param = '';
            let paramstrings = [];
            for (let i = 0; i < paramstring.length ; i++) {
                if (paramstring[i]!==""){
                    paramstrings.push(paramstring[i])
                }
            }
            for (let i = 0; i <(paramstrings.length - 1); i++) {
                param = param + paramstrings[i] + '/';
            }
            path.dataset.abpath = param;
            path.innerText = '...';
            if (jsondatass[0]==='' || jsondatass[0]===undefined || jsondatass[0]===null){
                path.dataset.abpath = jsondatass[0];
            }else {
                contain.appendChild(path);
            }

            path.addEventListener('click', chooseFileParent);
            let jsondatas=JSON.parse(jsondatass[1])
            for (var jsondata in jsondatas) {
                var li = document.createElement("file");
                console.log(jsondatas);
                // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
                var ss = jsondatas[jsondata];
                li.setAttribute("data", ss);
                li.dataset.locadpath = jsondatas[jsondata]
                var dv = document.createElement("dv");

                dv.innerText = ss;
                let a = document.createElement("a");
                let b = document.createElement("b");
                a.appendChild(dv);
                li.appendChild(a);
                li.appendChild(b);
                li.addEventListener('click', chooseFile);
                b.innerHTML = '<input type="checkbox" id="horns" name="horns" />';
                contain.appendChild(li);
            }
        });
    }


    function closechoose() {
        let node = this.parentNode;
        node.parentNode.removeChild(node);
        let lt=document.getElementById("listcontainer")
        lt.style=null;
    }

    function rightchoose() {
        let chooseinput = document.querySelectorAll(".dragContainer .orgin .choosefile input");
        let parentdoc = document.querySelector(".dragContainer .orgin docmentPath");
        let abpath;
        if (parentdoc == null) {
            abpath = "";
        } else {
            abpath = parentdoc.dataset.abpath;
        }

        let arr = [];
        for (let i = 0; i < chooseinput.length; i++) {
            if (chooseinput[i].checked) {
                arr.push(abpath + '/' + chooseinput[i].parentNode.parentNode.dataset.locadpath);
            }
        }
        let choosejson = JSON.stringify(arr)
        fetch("http://" + address + ":" + port + "/map/CloudeChoose/rightChoose", {
            method: 'post',
            body: choosejson
        })
            .then((Response) =>{
                if(Response.ok){
                    dragload()
                }
                return Response.json()
            }).then((json) => {
            console.log(json);
            return json;
        }).then(jsondatas => {
            //     dropeabled = document.querySelector(".right ul");
            //     for (var jsondata in jsondatas) {
            //         var li = document.createElement("li");
            //         console.log(jsondatas);
            //         // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
            //         var ss = JSON.stringify(jsondatas);
            //         li.setAttribute("data", ss);
            //         li.dataset.locadpath = jsondatas[jsondata]
            //         var dv = document.createElement("dv");
            //
            //         dv.innerText = jsondata;
            //         a = document.createElement("a");
            //         a.appendChild(dv);
            //         li.appendChild(a);
            //         dropeabled.appendChild(li);
            //
            //         li.setAttribute("ondragover", "handleDragover");
            //         li.setAttribute("ondragover", "handleDragleave");
            //         li.setAttribute("ondrop", "handleDrope");
            //     }
            // }).then(()=>{
            //     dragload()
        });
    }

    return addfile
}

