# IconProgressBar

## 带左右图标的自定义进度条

![](https://github.com/fringesy/IconProgressBar/blob/main/screenshots0.png)
![](https://github.com/fringesy/IconProgressBar/blob/main/screenshots.png)
![](https://github.com/fringesy/IconProgressBar/blob/main/screenshots1.png)

## xml属性设置

```xml
    <declare-styleable name="IconProgressBar">
        <!-- 背景色 -->
        <attr name="ipb_background_color" format="color" />
        <!-- 进度条颜色 -->
        <attr name="ipb_progress_color" format="color" />
        <!-- 指示器颜色 -->
        <attr name="ipb_indicator_color" format="color" />
        <!-- 指示器阴影 -->
        <attr name="ipb_layer_color" format="color" />
        <!-- 最大值 -->
        <attr name="ipb_max_value" format="float" />
        <!-- 当前值 -->
        <attr name="ipb_progress_value" format="float" />
        <!-- 左侧图标 -->
        <attr name="ipb_left_icon" format="reference" />
        <!-- 右侧图标 -->
        <attr name="ipb_right_icon" format="reference" />
    </declare-styleable>
```

## 代码设置

```kotlin
        findViewById<IconProgressBar>(R.id.ipb)
            .setOnProgress { progress ->
                //设置滑动进度回调
            }.setOnStop { endProgress ->
                //设置停止进度回调
            }
            .setMax(100f)//设置最大值
            .setProgress(30f)//设置当前进度
```
