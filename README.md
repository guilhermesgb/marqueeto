# marqueeto

EditTexts with floating labels and marquee ellipsize.

```
                                         `    `````````````````````
                     `;@@@+;':,,.``           ` ```````````````````
                 @@@@@@@@@@@@@@@@@@@@#:``  ` `  ```````````````````
              '@@@@@@@@@@@@@@@@@@@@@@@@@@+. ```````````````````````
           `'#@@@@@@@@@@@@@####++#@@@@@@@@@@;``````````````````````
        `@@@@@@@@@#+;,,,,,,,:,,,:::;+#@@@@@@@@; ```````````````````
       #@@@@+;';:,,,,,,,::::,::::::;''#@@@@@@@@@.``````````````````
   ``.@@@#':,,,,,,,,,,,,,,,,,,,::;;;''+#@@@@@@@@@;`````````````````
    .+##;,,,,,,,,,,,,,,,,,,,,,,:::;;:;;'+@@@@@@@@@@````````````````
   `'@+:,,,,,,,,,,....,,,,,,,,,,:::::;;;;+#@@@@@@@@+:;;.```````````
    :#,,,,,,,,,,......,,,,,,,,,,:::;::;;;;;;'@@@@@@++++',``````````
    ':,,,,,,.,........,.,,,,,,,,,::::;;:;;:;;;+@@@@#':;+;,`````````
   `',,,,,....,,..........,,,,:+++#+'';;:':;;;;'#@##;;;:':.````````
    +,,,,,,,.,...........,:'##+##+';;;':;:'::;;;'+++##;;:'.````````
  ``#;,,,,,,,,..........,:'';;,..,,,,:':+:;;;;;;'+#''+++;';````````
   .#+,,,,,............,::;;',,+@@@@+:;;;::::;;;'''';'':'':````````
 ``.'@:,,,,,,,,.,,..,,,,::;+''#++',,:':,,:::::;'''''';::::;````````
   `:++,,,,,:;''';:,,,,,,,'+#+':,;'':,,,::::::;''''''';;,:,````````
  ` .:#',:'+##;::;';;:,,,:;';;:;;::,,,,,,,::::;;''''''';',`````````
  ` `.'#;++';,:,'###'+:,,,:;;;::,,,,,,,,,:::;;';'''++'':....`.`````
```  `.##,;;::@#@+;;;;;:,,,:;;;;';;;;:::,:::;''+';''++'',,......```
```````:';::#+,,:;;;::::,,,,:;'';;'+';;'+++''+''';;'++++#',,,....``
````````..,::::::,,,:::,,,,,:::;;'+#';''++###;;;'';'++++@@#,+@#;...
``````````..,::,,,,,::;+:,,,,,;;+##+'''+####':,;'':'##++#@#.:++'+'.
`````````````::,,,,,:'#+;:,,,:'###''''##++';;::'+;:+##++#@:,;+''+++
``````````````::,:::;++:;''++++++'#+;;;'''';;::;+;;+##++#+,,'''''',
```````````````:;;;'+#+'''''''#;:,:;;;;';';;::;'+;'##++#;.,';+'+',.
````````````````;+'++#+#@@@#;;;:;;''''';;;;;;:;'+'+####',,;:'++',.`
`````````````````:'++++''+;'';;;':::::'';;;;;;'+#+####:,,;:'+''..``
``````````````````.''''';;;;;;;;';:::::''''';'+#+###@,,:;,;++',..`.
```````````.````````.'';'+';;;;;;;;:,:;++'+';+#####+,:;',;'+;...`.;
',``````...```.``.``.::'+#++';;;;;;;;''+++#++#@#@@;:::',:'+:,,..,''
:;';:::.`..`.....``::::'@@@@+#+;;;'+#+##@@##@@@;,,,:::,....,,.,,;++
::;;;''''''.`.``..;';:''@@@@++##########@@@@@;....,,;;;;...```..,;'
,::::;;;''++...`.;'#:::'#@@@###;+#@@@@@@@@@@:..``.`...,,:';.``.,:;'
,,,,:::::;'+:...;;;++#:'##@##+';+'@####@@@,,..`````````..,;'```.:;+
...,,,,,,::;+;.''+;;##:+:#@@@;;;';+#@###::,..``,.....```.,:'.``.:'+
```

## LabelledMarqueeEditText

This widget is an enhanced `EditText` (already with `TextInputLayout` support) which provide two modes:

* **write mode**: just like the normal `EditText`, with a floating label

* **read mode**: with a **nice marquee effect** if the text is longer than what can fit within the widget's bounds

### How to use

To use `LabelledMarqueeEditText` in your layouts, all you need to do is:

    <com.guilhermesgb.marqueeto.LabelledMarqueeEditText
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:inputType="phone"
        android:hint="Phone"
        labelled_marquee:iconKey="md-local-phone"
        labelled_marquee:mode="edit"
        labelled_marquee:labelledMarqueeEditTextStyle="@style/LabelledMarqueeEditTextCustom"
        tools:ignore="HardcodedText"/>

`LabelledMarqueeEditText`'s recommended width is either `match_parent` or a fixed value. If it is `wrap_content` then the width will vary depending on the text - that won't be a pleasant experience for the end user.

You can use either:

* `android:text` via XML or `setText()` programatically,
* `android:hint` via XML or `setHint()` programatically,
* `marqueeto:error` via XML or `setError()` programatically,
* `marqueeto:errorEnabled` via XML or `setErrorEnabled()` programatically,
* `android:inputType` via XML or `setInputType()` programatically,

just as you would normally; Additionally you can use:

* `marqueeto:iconKey` via XML or `setIconKey()` programatically to set the [Iconify Material](https://github.com/JoanZapata/android-iconify/blob/master/android-iconify-material/src/main/java/com/joanzapata/iconify/fonts/MaterialIcons.java) icon key to be used in the widget, e.g. `md-account-balance`;
* `marqueeto:mode` via XML or `setMode()` programatically to set the preferred mode (a.k.a the initial state of the widget upon creation), e.g. `edit` or `marquee`.
**Important**: if the widget has no text, it will always be in `edit` mode no matter what - even if `marqueeto:mode="marquee"`.

### Customize

`LabelledMarqueeEditText` can be highly customized! Below are all the attributes that can be changed either via XML or programatically:

* `android:textColor` via XML or `setTextColor()` programatically to set the text color in the widget;
* `marqueeto:errorColor` via XML or `setErrorColor()` programatically to set the error color in the widget.
* `marqueeto:labelledMarqueeEditTextStyle` via XML or `setCustomStyle()` programatically to change other more interesting appearance aspects of the widget, such as the icon, label and highlighted colors. A custom style defining the following attributes must be provided:

  * `<item name="baseColor"/>`\* to set the base color to be applied to the widget's overall appearance;
  * `<item name="highlightColor"/>`\* to set the color to be applied to the widget while it is highlighted (selected);
  * `<item name="iconColor"/>` to set the icon's color. If undefined, it will be equal to `baseColor`;
  * `<item name="labelColor"/>` to set the floating label's color. If undefined, it will be equal to `highlightColor`.
  
  \* You must provide this attribute in your custom style, otherwise it will be set to `android:color/black`.

### Special Thanks:

* [Iconify](https://github.com/JoanZapata/android-iconify)
* [RippleEffect](https://github.com/traex/RippleEffect)
* [Design Support Library](http://android-developers.blogspot.com.br/2015/05/android-design-support-library.html)
