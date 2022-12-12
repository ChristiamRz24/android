import 'package:asyncflutter/services/mockapi.dart';
import 'package:asyncflutter/widgets/button.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Sincronía en Flutter'),
      debugShowCheckedModeBanner: false,
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        centerTitle: true,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          // ignore: prefer_const_literals_to_create_immutables
          children: <Widget>[
            MyButton(
              duration: 1,
              image: 'fast.png',
              color: Colors.green,
              // containerColor: Colors.blue,
              fun: MockApi().getFerrariInteger,
            ),
            MyButton(
              duration: 4,
              image: 'medium.png',
              color: Colors.orange,
              // containerColor: Colors.blue,
              fun: MockApi().getHyundaiInteger,
            ),
            MyButton(
              duration: 8,
              image: 'slow.png',
              color: Colors.red,
              // containerColor: Colors.blue,
              fun: MockApi().getFisherPriceInteger,
            )
          ],
        ),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
