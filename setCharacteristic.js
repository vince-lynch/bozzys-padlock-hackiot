// Testing platform on here --> https://googlechrome.github.io/samples/web-bluetooth/write-descriptor.html?description=+++

var myDescriptor;
var myCharacteristic;
var myService;

function characteristicvaluechanged(a,b,c){
  console.log('characteristicvaluechanged', a, b, c);
}

function onReadButtonClick() {
  let serviceUuid = "0000ffd5-0000-1000-8000-00805f9b34fb";//document.querySelector('#service').value;
  if (serviceUuid.startsWith('0x')) {
    serviceUuid = parseInt(serviceUuid);
  }

  let characteristicUuid = "0000ffd9-0000-1000-8000-00805f9b34fb";//document.querySelector('#characteristic').value;
  if (characteristicUuid.startsWith('0x')) {
    characteristicUuid = parseInt(characteristicUuid);
  }

  log('Requesting any Bluetooth Device...');
  navigator.bluetooth.requestDevice({
   // filters: [...] <- Prefer filters to save energy & show relevant devices.
      acceptAllDevices: true,
      optionalServices: [serviceUuid]})
  .then(device => {
    console.log('device', device);
    log('Connecting to GATT Server...');
    return device.gatt.connect();
  })
  .then(server => {
    console.log('server', server);
    log('Getting Service...', server);
    return server.getPrimaryService(serviceUuid);
  })
  .then(service => {
    myService = service;
    console.log('service', service);
    log('Getting Characteristic...', service);
    return service.getCharacteristic(characteristicUuid);
  })
  .then(characteristic => {
    console.log('characteristic', characteristic);
    log('Getting Descriptor...', characteristic);
    myCharacteristic = characteristic;
    myCharacteristic.addEventListener('characteristicvaluechanged',
      characteristicvaluechanged);

    myCharacteristic.addEventListener('oncharacteristicvaluechanged', (a,b,c)=>{
      console.log('oncharacteristicvaluechanged', oncharacteristicvaluechanged);
    })

    return characteristic.getDescriptor('gatt.characteristic_user_description');
  })
  .then(descriptor => {
    console.log('descriptor', descriptor);
    document.querySelector('#writeButton').disabled =
        !descriptor.characteristic.properties.write;
    myDescriptor = descriptor;
    log('Reading Descriptor...', descriptor);
    return descriptor.readValue();
  })
  .then(value => {
    console.log('value', value);
    let decoder = new TextDecoder('utf-8');
    log('> Characteristic User Description: ' + decoder.decode(value));
  })
  .catch(error => {
    console.log('error', error);
    document.querySelector('#writeButton').disabled = true;
    log('Argh! ' + error);
  });
}

function setPassword(){
  console.log('setPassword');
  // from python example
  var raw= '29' + "123412" + '28';
  //message= binascii.unhexlify(raw)
  //# Send message
  //self.p.writeCharacteristic(0x4d,(message))

  let uint8Array = new Uint8Array([29,123412,28]);
  //myCharacteristic.writeValue(uint8Array)
  let encoder = new TextEncoder('utf-8');
  myCharacteristic.writeValue(encoder.encode(raw))
  .then(msg => {
    console.log('msg setPassword', msg);
    log('> Characteristic User setPasswordsetPasswordsetPassword changed to: ');
  })
  .catch(error => {
    log('Argh! ' + error);
  });
}

function onWriteButtonClick() {
  if (!myDescriptor) {
    return;
  }
  let extraCondition = true;
  //
  let bArr = [-2, 79, 80, 69, 78, 0, 0, 0, -16, -3]; //openArr
  let bArr2 = [-2, 67, 76, 79, 83, 69, 0, 0, -16, -3]; // closeArr


  if(extraCondition){
    bArr[8] = 0; // openArr
    bArr2[8] = 0;
  }
  let uint8Array = new Uint8Array(bArr);
  let decodedString = new TextDecoder().decode(uint8Array);


  alert( decodedString ); // Hello ?
  //
  let encoder = new TextEncoder('utf-8');
  let value = decodedString;//document.querySelector('#description').value;
  log('Setting Characteristic User Description...');
  
 
  //myCharacteristic.setWriteType(2).then(setWriteType =>{console.log('setWriteType', setWriteType)});
  //myCharacteristic.writeValue('\xfeOPEN\x00\x00\x00\x00\xfd')
  myCharacteristic.writeValue(encoder.encode('fe4f50454e00000000fd'))
  .then(msg => {
    console.log('msg', msg);
    log('> Characteristic User Description changed to: ' + value);
  })
  .catch(error => {
    log('Argh! ' + error);
  });
}