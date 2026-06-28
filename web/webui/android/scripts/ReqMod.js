
class SequentialFetchQueue {
    constructor() {
        this.queue = [];
        this.isProcessing = false;
    }

    /**
     * 将请求添加到队列中，并尝试启动处理过程
     * @param {Object} request - 包含url和可能的配置的对象
     * @param {Function} callback - 请求完成后的回调函数，接收响应数据或错误
     */
    enqueue(request, callback) {
        this.queue.push({ request, callback });
        this.tryToProcess();
    }

    /**
     * 尝试处理队列中的下一个请求
     */
    async tryToProcess() {
        if (this.isProcessing || this.queue.length === 0) {
            return;
        }

        this.isProcessing = true;

        const { request, callback } = this.queue.shift();

        try {
            const response = await fetch(request.url, request.options || {});
            const data = await response.json(); // 假设响应是JSON
            callback(null, data); // 调用回调，传入null作为错误（表示成功）和响应数据
        } catch (error) {
            callback(error, null); // 调用回调，传入错误和null作为响应数据
        }

        this.isProcessing = false;
        this.tryToProcess(); // 递归调用以处理下一个请求（如果有的话）
    }
}

function f() {
    (async () => {
        const module = await import(/* webpackChunkName: "sequentialFetch" */ 'path/to/sequentialFetch.js');
        // 现在可以使用 module.default 或其他导出的内容
    })();
}
// 导出SequentialFetchQueue类
export default SequentialFetchQueue;

// 为了让这个示例更有用，我们可以稍微修改它，以便在函数体内做一些事情：
//
// javascript
// (function(context, callback) {
//     console.log("Context:", context); // 打印传入的上下文
//     if (typeof callback === 'function') {
//         callback("Hello from callback!"); // 如果第二个参数是函数，则调用它
//     }
// })(this, function(message) {
//     console.log(message); // 输出: Hello from callback!
// });
// 在这个扩展的示例中，外层函数接收两个参数：context（上下文）和 callback（回调函数）。
// 函数体内首先打印了上下文对象，然后检查 callback 是否为函数，如果是，则调用它并传入一个字符串参数。
// 这样，你就可以看到如何在这个模式下使用传入的参数和函数了。
