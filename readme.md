# gamebot

**gamebot is in early stage**

Gamebot is a framework for automating android app and game with rust. Usually it's done in scripting language like lua/javascript/python using closed source framework. And it's not easy to use custom image processing algorithms or custom neural network. Gamebot tries to provide another option for android automation with more possibilities.

## quick glance

```rust
use gamebot::{api::img, find::Find};

fn collect_mail() -> Option<()> {
    let mail_btn = img("asset/mail.jpg").within((0, 0, 100, 100));
    mail_btn.appear(2);
    mail_btn.find()?.click();
    Some(())
}
```

## feature

1. rust language and ecosystem
1. git based auto update
1. root and non-root(need shizuku) android >= 7.0
1. neural network inference with onnxruntime and ncnn
1. multilingual ocr for singleline and multiline text
1. fast screenshot and accessibility node analysis
1. jetpack compose based ui and webview ui
