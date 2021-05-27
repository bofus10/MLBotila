# MLBotila
_ML Arg Bot to monitor track respond sales and answers, manage your account._
_You will be able to see new questions, sales and messages, as well as reply to them_

## [ Work in Progress🛠️ ]

## Pre-requisits 📋

* _You will need to compile the code and generate a jar file for execution_
* _You will need to create an API-SCR on [Developers-Mercadolibre](https://developers.mercadolibre.com.ar/es_ar/api-docs-es)_


## Setup 🔧

```
java -jar compiled_jar
```

### Content config.properties ⚙️
* bot_Token=FILL_WITH_BOT_TOKEN
* ChatID=FILL_WITH_CHAT_ID


* SellerID=ML_SELLER_ID
* seller_email=ML_SELLER_EMAIL

##App
* APP_ID=ML_REG_APP_ID


## Usage 🚀

_The usage is mainly via Telegram Bots, all message will get to you automatically_

_Once you set your bot you can ask for help by typing "help"_

_Until Auth2token is working you will be asked for a new token everytime the in-use one expires_

__Comandos:__

* G#{preguntas|ventas|ventasall|mensajes}
  * Devolvera las Preguntas o Mensajes sin responder. En el caso de las ventas devuelve todas por el momento.
* Q{ID}#{TEXTO}
  * EJ: Q11#Efectivamente, podes hacer la compra. Saluda
* Metodo para responder las Preguntas segun el ID
* M{ID}#{TEXTO}
  * EJ: M11#Si necesitas otro , tenemos 2!
* Metodo para responder los Mensajes de las VENTAS segun el ID
  * V{ID}#{TEXTO}
* Envia un Mensaje a la Venta Elegida
  * EJ: V22#Gracias por tu Compra!
* T#{TOKEN}
* Deben entrar en la web que se envia, con al APP ID que se pasa y obtener el nuevo token. Luego responderlo:
  * T#APP_USR-TOKEN.. 


## License 📄

Project License type:  GPL-3.0 License - Check [LICENSE](LICENSE) file for more details.
