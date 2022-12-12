import 'package:flutter/material.dart';

class MyButton extends StatefulWidget {
  // Parámetros requeridos
  final Function fun;
  final String image;
  final Color color;
  final int duration;
  // final Color containerColor;

  const MyButton({
    super.key,
    required this.fun,
    required this.image,
    required this.color,
    required this.duration,
    // required this.containerColor,
  });

  @override
  State<MyButton> createState() => _MyButtonState();
}

class _MyButtonState extends State<MyButton> {
  // Variables
  bool selected = false;
  bool finished = false;
  int dataHelp = 0;
  int data = 0;

  Future<int> getData() async {
    return await widget.fun();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      // Escuhar el click del usuario
      onTap: () {
        setState(() {
          /* Cuando el usuario presiona el botón se cambia el estado de este de
           * seleccionado a verdadero, lo cual hace que se anime el contenedor.
           * Además el estado de finalizado de la animación se pone en falso para
           * que no se muestre el valor de obtenido de la petición en getData 
           * hasta que termine de animarse el contenedor.
           * */
          selected = true;
          finished = false;
          /* dataHelp es una variable auxiliar que se usa para guardar el valor
           * anterior obtenido de la petición en getData, esto con el fin de que
           * solo se muestre el valor nuevo si la animación ya ha finalizado.
           * */
          dataHelp = data;
          getData().then((value) => setState(() {
                data = value;
              }));
        });
      },
      child: Container(
        padding: const EdgeInsets.all(10.0),
        // Dar bordes redondeados al contenedor
        decoration: const BoxDecoration(
          // borderRadius: const BorderRadius.all(Radius.circular(25)),
          // color: widget.containerColor,
          color: Colors.transparent,
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Padding(
              padding: const EdgeInsets.only(bottom: 5.0),
              child: Container(
                width: 75.0,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: widget.color,
                ),
                child: Image.asset(
                  widget.image,
                  height: 80,
                  width: 80,
                  // Padding para que la imagen no se pegue al borde del contenedor
                ),
              ),
            ),
            AnimatedContainer(
              /* Si el botón tiene el estado seleccionado en verdadero,
               * el ancho se agranda a 300 en x segundos, de lo contrario,
               * el ancho se reduce a 0 en 1 milisegundo mediante la
               * animación del contenedor.
               * */
              width: selected == true ? 300.0 : 0.0,
              height: 25.0,
              color: widget.color,
              alignment: AlignmentDirectional.center,
              duration: selected == true
                  ? Duration(seconds: widget.duration)
                  : const Duration(milliseconds: 1),
              onEnd: () => setState(() {
                /* Cuándo la animación termina, se le permite al usuario volver a
                 * presionar el botón cambiando el estado de seleccionado de este a falso.
                 * Además, se muestra el valor obtenido de la petición en pantalla cambiando
                 * el estado de finalizado de la animación a verdadero.
                 * */
                selected = false;
                finished = true;
              }),
            ),
            Text(
              // Mostrar el dato obtenido en la petición si ya ha terminado la animación
              finished == true ? '$data' : '$dataHelp',
              style: TextStyle(
                fontSize: 40,
                color: widget.color,
                fontWeight: FontWeight.bold,
              ),
            )
          ],
        ),
      ),
    );
  }
}
