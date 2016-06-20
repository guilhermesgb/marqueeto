# marqueeto

EditTexts with floating labels and marquee ellipsize.

![marqueeto GIF](https://github.com/guilhermesgb/marqueeto/blob/master/demo.gif)

### Download via Gradle

Just add the following line to the dependencies section of your `build.gradle`:

``` gradle
compile 'com.github.guilhermesgb:marqueeto:1.1.10'
```

## LabelledMarqueeEditText

This widget is an enhanced `EditText` (already with `TextInputLayout` support) which provides two modes:

* **read mode**: displays a **nice marquee effect** if the text is longer than what can fit within the widget's bounds

![marqueeto read mode](https://github.com/guilhermesgb/marqueeto/blob/master/read mode.png)
![marqueeto read mode](https://github.com/guilhermesgb/marqueeto/blob/master/read mode with marquee.png)
\* read mode with a marquee effect, when necessary

* **write mode**: just like the normal `EditText` with `TextInputLayout` support (a.k.a. with a floating label)

![marqueeto transition](https://github.com/guilhermesgb/marqueeto/blob/master/transition from read to write mode.png) ![marqueeto write mode](https://github.com/guilhermesgb/marqueeto/blob/master/write mode.png)
\* widget transitioning into write mode

Visually, you can tell the current mode the widget is at by the presence or absence of the bottom bar, and also from the size of the accompanying icon, which is a bit larger while in write mode.

### Niceties

* `LabelledMarqueeEditText` works consistently from **API 14** onwards (14, 16, 17 and 22 have been tested);
* **Has the same Material look 'n feel throughout all supported API levels** (even the selector drawables!);
* Even though support for the official Material icons is default, thanks to the awesome [Iconify](https://github.com/JoanZapata/android-iconify) library, **pretty much any icon can be supported**, even your own! If this interests you, refer to their project to find out how;
* Your project won't have to have the Support Libraries (AppCompat, Design, etc.) as a dependency for this widget to work (so you can use this "enhanced `TextInputLayout` + `EditText` combination in places where they wouldn't work without these dependencies, e.g. a project without these support libs and simply good and old activities extending `Activity`).

### How to use in your projects

To use `LabelledMarqueeEditText` in your layouts, all you need to do is declare usage of this widget:

``` xml
    <com.github.guilhermesgb.marqueeto.LabelledMarqueeEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="text"
        android:hint="Address"
        labelled_marquee:mode="edit"/>
```

`LabelledMarqueeEditText`'s recommended width is either `match_parent` or a fixed value. If it is set to `wrap_content`, then the actual width of the widget will vary depending on the underlying text - that probably won't be a pleasant experience for the end user, so you might want to avoid it.

You can use the following attributes:

* `android:text` via XML or `setText()` programatically,
* `android:hint` via XML or `setHint()` programatically,
* `marqueeto:error` via XML or `setError()` programatically,
* `marqueeto:errorEnabled` via XML or `setErrorEnabled()` programatically,
* `android:inputType` via XML or `setInputType()` programatically,

just as you would normally; Additionally you can use:

* `marqueeto:iconKey` via XML or `setIconKey()` programatically to set the [Iconify Material](https://github.com/JoanZapata/android-iconify/blob/master/android-iconify-material/src/main/java/com/joanzapata/iconify/fonts/MaterialIcons.java) icon key to be used in the widget, e.g. `md-account-balance`;
* `marqueeto:iconGravity` (new!) via XML only to set the icon gravity - either left or right is allowed;
* `marqueeto:mode` via XML or `setMode()` programatically to set the preferred mode (a.k.a the initial state of the widget upon creation), e.g. `edit` or `marquee`.
**Important**: if the widget has no text, it will always be in `edit` mode no matter what - even if `marqueeto:mode="marquee"`.

### Customize

`LabelledMarqueeEditText` can be highly customized! Below are all the attributes that can be changed either via XML or programatically:

* `android:textColor` via XML or `setTextColor()` programatically to set the text color in the widget;
* `marqueeto:labelledMarqueeEditTextStyle` via XML or `setCustomStyle()` programatically to change other more interesting appearance aspects of the widget, such as the icon, label and highlighted colors. A custom style defining the following attributes must be provided:

  * `<item name="baseColor"/>` to set the base color to be applied to the widget's overall appearance. If undefined, it will be equal to the Android default theme's `colorPrimary`;
  * `<item name="highlightColor"/>` to set the color to be applied to the widget while it is highlighted. If undefined, it will be equal to the Android default theme's `colorAccent`;
  * `<item name="iconColor"/>` to set the icon's color while the widget is not highlighted. If undefined, it will be equal to `baseColor`. Once the widget is highlighted, the icon's color will always be equal to `highlightColor`;
  * `<item name="errorColor"/>` to set the error color in the widget. If undefined, it will be equal to `highlightColor`.

### Examples

Declaring a LabelledMarqueeEditText for inputting phone numbers (using the Iconify Material icon `md-local-phone`):

``` xml
    <com.github.guilhermesgb.marqueeto.LabelledMarqueeEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="phone"
        android:hint="Phone Number"
        labelled_marquee:iconKey="md-local-phone"
        labelled_marquee:mode="edit"
        android:textColor="@android:color/white"
        labelled_marquee:errorColor="@android:color/holo_purple"
        labelled_marquee:labelledMarqueeEditTextStyle="@style/MyCustomStyle"
        tools:ignore="HardcodedText"/>
```

Note that it is using a custom style referenced through `@style/MyCustomStyle` for customizing the widget, so below is an example of such style:

``` xml
    <style name="MyCustomStyle">
        <item name="baseColor">@android:color/holo_red_light</item>
        <item name="highlightColor">@android:color/holo_red_dark</item>
    </style>
```

## Special Thanks

* [Design Support Library](http://android-developers.blogspot.com.br/2015/05/android-design-support-library.html) by Google
* [Iconify](https://github.com/JoanZapata/android-iconify) by Joan Zapata
* [RippleEffect](https://github.com/traex/RippleEffect) by Robin Chutaux

## License

```
Copyright 2015 Guilherme Baptista

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

It uses The Android Open Source Project's Support Libraries by Google,
and Iconify by Joan Zapata, both licensed under Apache 2.0 as well,
and RippleView by Robin Chutaux, licensed under MIT,
which is compatible with this library's license.

    https://opensource.org/licenses/MIT
```
