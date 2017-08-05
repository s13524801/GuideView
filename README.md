# GuideView

<img src="https://github.com/s13524801/GuideView/blob/master/S70621-162239.jpg" alt="预览图" title="预览图" width="280" height="480" />

## 支持配置属性

    1、外部点击是否关闭 setTouchOutsideDismiss
    2、控件高亮形状类型圆形、椭圆形、矩形
    3、蒙层颜色
    4、高亮控件内外边距
    5、自定义高亮控件显示位置

## 使用方法

    GuideView.builder(this)
            .addHighLight(targetRight, R.drawable.arrow_down_right, R.drawable.tip, GuideView.GRAVITY_LEFT_TOP)
            .addHighLight(targetMid, R.drawable.arrow_up_left, R.drawable.tip, GuideView.GRAVITY_BOTTOM, GuideView.SHAPE_RECT)
            .show();


## 参考

[HighLightGuideView](https://github.com/jaydenxiao2016/HighLightGuideView)
