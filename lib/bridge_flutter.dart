import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';

class bridge_flutter {
  var plat = MethodChannel('samples.flutter.dev/Login');

  var retorno = 0;

  void modeProducto() {
    this.plat = MethodChannel('samples.flutter.dev/Productos');
  }

  void modeCompra() {
    this.plat = MethodChannel('samples.flutter.dev/Compra');
  }

  void modeMain() {
    this.plat = MethodChannel('samples.flutter.dev/Login');
  }

  void modeVenta() {
    this.plat = MethodChannel('samples.flutter.dev/Venta');
  }

  Future Call(String Acc) async {
    try {
      var result = await plat.invokeMethod(Acc, []);
      result = ' $result.';
    } on PlatformException catch (e) {}
    setState() {
      retorno = retorno;
    }
  }

  Future CallOne(String Acc, var a) async {
    try {
      var result = await plat.invokeMethod(Acc, [
        {"Val1": a},
      ]);
      result = ' $result.';
    } on PlatformException catch (e) {}
    setState() {
      retorno = retorno;
    }
  }

  Future CallTwo(String Acc, var a, var b) async {
    try {
      var result = await plat.invokeMethod(Acc, [
        a, b
      ]);
      return result.toString();
    } on PlatformException catch (e) { print(e); }
  }

  Future CallThree(String Acc, var a, var b, var c) async {
    try {
      var result = await plat.invokeMethod(Acc, [
        a, b, c
      ]);
      return result.toString();
    } on PlatformException catch (e) { print(e); }
  }
}
