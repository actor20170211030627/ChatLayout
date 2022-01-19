# ChatLayout
聊天的emoji...
 <ul>
     <li><a href="https://github.com/actor20170211030627/ChatLayout">Github</a></li>
     <li><a href="https://gitee.com/actor20170211030627/ChatLayout">Gitee码云(国内网速更快)</a></li>
 </ul>

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Screenshot
#### 1.chatlayout ↓
<img src="captures/chatlayout.png" width=35%></img>

#### 2.example gif
<img src="captures/example.gif" width=35%></img>

## Demo
<a href="app/build/outputs/apk/debug/app-debug.apk" target="_blank">download apk</a> or scan qrcode:  <br/>
<img src="captures/qrcode.png" width=35%></img>

## Usage
**1.**如何使用emoji, 可参考Demo
<pre>
Demo中添加了MyAndroidFrameWork依赖, 里面有一个ChatLayout聊天控件, 见 <a href="app/build.gradle" target="_blank">build.gradle</a>
</pre>

**2.** 在Application中初始化
<pre>
参考: <a href="app/src/main/java/com/chatlayout/example/Application.java" target="_blank">Application</a>
</pre>

**3.** 布局文件中xml
<pre>
参考: <a href="app/src/main/res/layout/activity_main.xml" target="_blank">activity_main.xml</a>
</pre>

**4.** Activity中
<pre>
参考: <a href="app/src/main/java/com/chatlayout/example/activity/MainActivity.java" target="_blank">MainActivity.java</a>
</pre>


## How to
To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
<pre>
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
</pre>


**Step 2.** Add the dependency, the last version:
[![](https://jitpack.io/v/actor20170211030627/ChatLayout.svg)](https://jitpack.io/#actor20170211030627/ChatLayout)

    android {
      ...
      compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
      }
    }

    dependencies {
		implementation 'com.google.android.material:material:your_version'
		implementation 'androidx.constraintlayout:constraintlayout:your_version'

        //https://gitee.com/actor20170211030627/ChatLayout
        implementation 'com.gitee.actor20170211030627:ChatLayout:version'
    }

## Thanks
<ul>
    <li>keyboard from: <a href="https://github.com/604982372/InputConflict" target="_blank">604982372/InputConflict</a> </li>
    <li>emoji from: <a href="https://github.com/tencentyun/TIMSDK" target="_blank">tencentyun/TIMSDK</a></li>
</ul>

## License
 Apache 2.0.
