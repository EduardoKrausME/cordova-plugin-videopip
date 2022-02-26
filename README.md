# Video Picture in Picture Plugin

Este plugin do Cordova habilita o Picture in Picture para android > 8.0

Baseado no [cordova-plugin-pip](https://www.npmjs.com/package/cordova-plugin-pip). 

## Install
```
cordova plugin add https://github.com/EduardoKrausME/cordova-plugin-videopip
```

## API Methods:

### enterPip

Faz com que o Vídeo entre em PIP 

```javascript
document.addEventListener ( "pause", function () {
    VideoPictureInPicture.enterPip(200,400,
          function(){console.log("Saiu do APP e entrou em Pip Mode")},
          function(error){console.log(error)});
}, false );
```
    
### isPip

Checa se esta em Pip Mode. Retorna ``true`` ou ``false`` na função sucesso.

```javascript
VideoPictureInPicture.isPip(
  function(result){console.log(result)},
  function(error){console.log(error)});
```

### isSupported

Checa se o Pip Mode é suportado. Retorna ``true`` ou ``false`` na função sucesso.

```javascript
VideoPictureInPicture.isSupported(
  function(result){console.log(result)},
  function(error){console.log(error)});
```

### onChanged

Escuta 

```javascript
VideoPictureInPicture.onChanged(
  function(result){console.log(result)},
  function(error){console.log(error)});
```

