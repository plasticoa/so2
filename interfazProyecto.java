import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class interfazProyecto {
    public static void main(String[] args) throws InterruptedException {
        interfaz();
    }
    static  JLabel labelClientesPerididos;
    static JLabel labelClientesEspera;
    static JLabel labelAtendidos;
    static JLabel labelLlegada;
    static JLabel labelAtencion;
    static JLabel labelBarbero;
    static JLabel labelBarbero2;
    static JLabel labelBarbero3;
    static JLabel label;
    static JLabel label2;
    static JLabel label3;
    static JPanel p3;
    static DefaultListModel<String> listModel2;
    static DefaultListModel<String> listModel;
    public static void interfaz() throws InterruptedException {
        Corre hilos = new Corre();
        JFrame frame = new JFrame("Barberia");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(1500, 1000);
        frame.setLayout((new BorderLayout(10,10)));
        frame.setVisible(true);

        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1, 2));
        frame.add(p1, BorderLayout.NORTH);

        labelAtendidos = new JLabel("Total clientes atendidos: ");
        p1.add(labelAtendidos);

        JLabel labelInicio = new JLabel("Hora de inicio: " + new Date());
        p1.add(labelInicio);

        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(4, 3,5,5));
        frame.add(p2, BorderLayout.CENTER);

        p3=new JPanel();
        p3.setLayout(new GridLayout(3,1));
        frame.add(p3,BorderLayout.SOUTH);

        labelBarbero = new JLabel("Barbero estado: Despierto ");
        p2.add(labelBarbero);

        labelBarbero2 = new JLabel("Barbero estado: Despierto");
        p2.add(labelBarbero2);

        labelBarbero3 = new JLabel("Barbero estado: Despierto");
        p2.add(labelBarbero3);

        ImageIcon image = new ImageIcon("C:\\Users\\plast\\Downloads\\avatar1.png");
        label = new JLabel(image);
        p2.add(label);

        ImageIcon image2 = new ImageIcon("C:\\Users\\plast\\Downloads\\avatar1.png");
        label2 = new JLabel(image2);
        p2.add(label2);

        ImageIcon image3 = new ImageIcon("C:\\Users\\plast\\Downloads\\avatar1.png");
        label3 = new JLabel(image3);
        p2.add(label3);

        ImageIcon imageSilla = new ImageIcon("C:\\Users\\plast\\Downloads\\A.jpg");

        //meter
        //labelSilla1 = new JLabel(imageSilla);
        //p3.add(labelSilla1);

        labelClientesPerididos = new JLabel("Clientes perdidos: ");
        p3.add(labelClientesPerididos);

        labelClientesEspera = new JLabel("Clientes en espera: ");
        p3.add(labelClientesEspera);



        JLabel temporizador1 = new JLabel("Tiempo: ");
        p2.add(temporizador1);
        JLabel temporizador2 = new JLabel("Tiempo: ");
        p2.add(temporizador2);
        JLabel temporizador3 = new JLabel("Tiempo: ");
        p2.add(temporizador3);

        labelLlegada = new JLabel("Velocidad de llegada: ");
        frame.add(labelLlegada, BorderLayout.WEST);

        labelAtencion = new JLabel("Velocidad de llegada: ");
        frame.add(labelAtencion, BorderLayout.EAST);



        //jlist de logs barberia
        listModel2 = new DefaultListModel<>();
        JList<String> list2 = new JList<>(listModel2);
        list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list2.setLayoutOrientation(JList.VERTICAL);
        list2.setVisibleRowCount(-1);
        JScrollPane listScroller2 = new JScrollPane(list2);
        listScroller2.setPreferredSize(new Dimension(250, 80));
        p3.add(listScroller2);

        listModel2.addElement("Logs Barberos");

        //jlist de logs clientes
        listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 80));
        p3.add(listScroller);
        listModel.addElement("Logs Clientes");




        hilos.corre();
        //check documentacion
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        final Runnable runnable = new Runnable() {

            int countdownStarter1 = 10;
            int countdownStarter2 = 30;
            int countdownStarter3 = 20;
            public void run() {

                temporizador3.setText("Tiempo (segundos): " + countdownStarter3);
                countdownStarter3--;

                temporizador2.setText("Tiempo (segundos): " + countdownStarter2);

                countdownStarter2--;

                temporizador1.setText("Tiempo (segundos): " + countdownStarter1);
                countdownStarter1--;

                //switch case stament
                if (countdownStarter1 < 0) {
                    temporizador1.setText("Tiempo (segundos): "+countdownStarter1+ " durmiendo");
                    labelBarbero.setText("Barbero estado: Dormido ");
                    ImageIcon image = new ImageIcon("C:\\Users\\plast\\Downloads\\levi dormido.jpg");
                    label.setIcon(image);
                }

                if (countdownStarter2 < 0) {
                    temporizador2.setText("Tiempo (segundos): "+countdownStarter1+ " durmiendo");
                    labelBarbero2.setText("Barbero estado: Dormido ");
                    ImageIcon image2 = new ImageIcon("C:\\Users\\plast\\Downloads\\levi dormido.jpg");
                    label2.setIcon(image2);
                }
                if (countdownStarter3 < 0) {
                    temporizador3.setText("Tiempo (segundos): " + 0 +" durmiendo");
                    labelBarbero3.setText("Barbero estado: Dormido ");
                    ImageIcon image3 = new ImageIcon("C:\\Users\\plast\\Downloads\\levi dormido.jpg");
                    label3.setIcon(image3);
                }

            }
        };
        scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);


    }

    public void updateAtendidos(int atendidos){
        labelAtendidos.setText("Total clientes atendidos: " + atendidos);
    }

    public void updateLlegada(int minLlegada, int maxLlegada, int tiempoLlegada){
        labelLlegada.setText("Llegada: Aleatorio [" + minLlegada+" - "+maxLlegada+"]"+ " obtenido "
                + tiempoLlegada/1000);
    }

    public void updateAtencion(int minAtencion, int maxAtencion, int tiempoAtencion){
        labelAtencion.setText("Atencion: Aleatorio [" + minAtencion+" - "+maxAtencion+"]"+ " obtenido "
                + tiempoAtencion/1000);
    }

    public void estadoLabelBarbero(String estado, int idBarbero){
        switch(idBarbero) {
            case 1:
                labelBarbero.setText("Barbero estado: " + estado);
                break;
            case 2 :
                labelBarbero2.setText("Barbero estado: " + estado);
                break;
            case 3 :
                labelBarbero3.setText("Barbero estado: " + estado);
                break;
            default:
               break;
        }
    }
    public void estadoImagenBarbero(Boolean estado,int idBarbero){
        if(estado){
            //ImageIcon image = new ImageIcon("C:\\Users\\plast\\Downloads\\avatar1.png");
            //label.setIcon(image);
            ImageIcon image = new ImageIcon("C:\\Users\\plast\\Downloads\\avatar1.png");
            switch(idBarbero) {
                case 1:
                    label.setIcon(image);
                    break;
                case 2 :
                    label2.setIcon(image);
                    break;
                case 3 :
                    label3.setIcon(image);
                    break;
                default:
                    break;
            }
        }else{
            ImageIcon image = new ImageIcon("C:\\Users\\plast\\Downloads\\levi dormido.jpg");
            switch(idBarbero) {
                case 1:
                    label.setIcon(image);
                    break;
                case 2 :
                    label2.setIcon(image);
                    break;
                case 3 :
                    label3.setIcon(image);
                    break;
                default:
                    break;
            }
        }
    }
    public void estadoSilla(int cantidadSillas){

        labelClientesEspera.setText("Clientes en espera: " + cantidadSillas);

    }
    public void setLabelClientesPerididos(int cliente) {
        labelClientesPerididos.setText("Clientes perdidos: " + cliente);
    }
    public void agregarElementoListaBarbero(String elemento){
        listModel2.addElement("Log Barberos: " + elemento);
    }
    public void agregarElementoListaCliente(String elemento){
        listModel.addElement("Log Barberos: " + elemento);
    }

    }


