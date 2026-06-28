// Modules to control application life and create native browser window
const { app, BrowserWindow } = require('electron')
const path = require('node:path')

function createWindow () {
  // Create the browser window.
  const mainWindow = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js') ,

      nodeIntegration: true,//是否集成node
      // devTools:false,//是否开启 DevTools
      webSecurity: false//是否禁用同源策略(上线时删除此配置)
    }
  })
  // win.loadURL("http://127.0.0.1:8877/test/index")
  // and load the index.html of the app.
  // mainWindow.loadFile('index.html')
  // win.loadURL("http://www.baidu.com")
  mainWindow.loadURL('http://127.0.0.1:8016' )///static/Menu-main/main.html
  // Open the DevTools.
  // mainWindow.webContents.openDevTools()
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.whenReady().then(() => {
  createWindow()

  app.on('activate', function () {
    // On macOS it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })
})

// Quit when all windows are closed, except on macOS. There, it's common
// for applications and their menu bar to stay active until the user quits
// explicitly with Cmd + Q.
app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
})

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.


//解决10.X版本跨域不成功问题(上线删除)
app.commandLine.appendSwitch('disable-features', 'OutOfBlinkCors');
