import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.*;

import java.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by 45858000w on 08/02/17.
 */
public class HiloPeticion extends Thread{

    private Socket newSocket;
    public PrintWriter salida;

    public HiloPeticion(Socket newSocket) {

        this.newSocket = newSocket;
    }

    @Override
    public void run() {
        InputStream is = null;
        try {
            /*is = newSocket.getInputStream();

            OutputStream os = newSocket.getOutputStream();

            byte[] mensaje = new byte[50];
            is.read(mensaje);



            System.out.println("  -> Mensaje recibido : "+ new String (mensaje));



            PrintWriter salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(newSocket.getOutputStream())), true);

            salida.println("  -> El resultado es :-> "+new String (mensaje).trim());

            System.out.println("  -> IP: "+newSocket.getInetAddress());

            System.out.println("  -> Cerrando el socket");

            newSocket.close();

            System.out.println("Cerrando el socket servidor");*/


            BufferedReader in = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
            salida = new PrintWriter(new OutputStreamWriter(newSocket.getOutputStream(), "8859_1"), true);

            String cadena = "";        // cadena donde almacenamos las lineas que leemos
            int i = 0;                // lo usaremos para que cierto codigo solo se ejecute una vez

            do {
                cadena = in.readLine();

                if (cadena != null) {
                    // sleep(500);
                    System.out.println(" --" + cadena + "- ");
                }


                if (i == 0) // la primera linea nos dice que fichero hay que descargar
                {
                    i++;

                    StringTokenizer st = new StringTokenizer(cadena);

                    if ((st.countTokens() >= 2) && st.nextToken().equals("GET")) {
                        retornaFichero(st.nextToken());
                    } else {
                        salida.println("400 Petición Incorrecta");
                    }
                }

            }
            while (cadena != null && cadena.length() != 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    void retornaFichero(String sfichero)
    {
        System.out.println("Recuperamos el fichero " + sfichero);

        // comprobamos si tiene una barra al principio
        if (sfichero.startsWith("/"))
        {
            sfichero = sfichero.substring(1) ;
        }

        // si acaba en /, le retornamos el index.htm de ese directorio
        // si la cadena esta vacia, no retorna el index.htm principal
        if (sfichero.endsWith("/") || sfichero.equals(""))
        {
            sfichero = sfichero + "index.htm" ;
        }

        try
        {

            // Ahora leemos el fichero y lo retornamos
            File mifichero = new File(sfichero) ;

            if (mifichero.exists())
            {
                salida.println("HTTP/1.0 200 ok");//no se puede quitar esta linea, sino no funciona
                salida.println("Servidor OK! --->>>  Server: Cristian Javier Ramirez -> Date: " + new Date());
                salida.println("Content-Type: text/html");
                salida.println("Content-Length: " + mifichero.length());
                salida.println("\n");



                BufferedReader ficheroLocal = new BufferedReader(new FileReader(mifichero));


                String linea = "";

                do
                {
                    linea = ficheroLocal.readLine();

                    if (linea != null )
                    {
                        // sleep(500);
                        salida.println(linea);
                    }
                }
                while (linea != null);

                System.out.println("fin envio fichero");

                ficheroLocal.close();
                salida.close();

            }  // fin de si el fiechero existe
            else
            {
                System.out.println("No encuentro el fichero " + mifichero.toString());
                salida.println("HTTP/1.0 400 ok");
                salida.close();
            }

        }
        catch(Exception e)
        {
            System.out.println("Error al retornar fichero");
        }

    }




}
