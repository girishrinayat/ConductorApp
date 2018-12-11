package com.ycce.kunal.conductorapp;

public class BusStops {

    private String[] pardiycce={"","Pardi", "subhan nagar", "old pardi naka", "railway crossing", "sangharsh nagar","swaminarayan mandir", "wathoda" , "karbi",
            "dighori flyover","mhalgi nagar", "manevada square","omkar nagar","rameshawari", "Tukram hall", "narendra nagar",
            "chatrapati square","sawrakar square", "pratap nagar", "padole hospital square","sambhaji square", "NIT garden",
            "Trimurti nagar","mangalmurti square","balaji nagar", "mahindra company", "IC square", "electric zone", "Hingna T-point", "crpf", "Ycce","Wanadongri"};

    private String[] buldiButebori={"","Buldi", "Dhantoli", "Lokmat Square", "Rahate Colony", "Jail Gate", "Ajni","Sai Mandir", "Chatrapatti", "Sneh Nagar", "Rajeev Nagar",
            "Somalwada","Ujjwal Nagar", "Sonegaon", "Airport(Pride Hotel)", "Bara Kholi", "Shivangaon","Chinchbhavan", "Khapri Naka", "Khapri", "Khapri Fata", "Parsodi",
            "Gauvsi Manapur", "Jamtha", "Ashokvan", "Dongargaon", "Gothali", "Mohgaon","Satgaon Fata", "Butibori" };

    private String[] buldiKoradi={"not yet  build"};

    private String[] buldiCRPF={"not yet build"};

    private String source;
    private String destination;
    private String route;
    private int startpos ;
    private int desPos ;



    public BusStops(String source, String destination){
        this.source = source;
        this.destination = destination;

    }

    //finding index position 0f source and destination to find route
    void checkRoute(){

        if(source != null && destination != null){


              if (startpos==0|| desPos==0){
//                  startpos = 0;desPos = 0;
                  for(int i=0;i<pardiycce.length;i++){

                      if (source.equals(pardiycce[i])){
                          this.startpos = i;
                      }
                      if(destination.equals(pardiycce[i])) {
                          this.desPos = i;
                      }
                      if(startpos!=0&& desPos!=0){
                          route = "pardiycce";
                      }


                  }
              }
                if (startpos==0|| desPos==0){
//                  startpos = 0;desPos = 0;
                for(int i=0;i<buldiButebori.length;i++){
                    if (source.equals(buldiButebori[i])){
                        this.startpos = i;
                    }
                    if(destination.equals(buldiButebori[i])) {
                        this.desPos = i;
                    }
                    if(startpos!=0&& desPos!=0){
                        route = "buldiButebori";
                    }
                }
              }
               if (startpos==0|| desPos==0) {
//                  startpos = 0;desPos = 0;
                  for (int i = 0; i < buldiKoradi.length; i++) {

                      if (source.equals(buldiKoradi[i])) {
                          this.startpos = i;
                      }
                      if (destination.equals(buldiKoradi[i])) {
                          this.desPos = i;
                      }
                      if(startpos!=0&& desPos!=0){
                          route = "buldiKoradi";
                      }
                  }
              }
               if (startpos==0|| desPos==0) {
//                  startpos = 0;desPos = 0;
                  for (int i = 0; i < buldiCRPF.length; i++) {

                      if (source.equals(buldiCRPF[i])) {
                          this.startpos = i;
                      }
                      if (destination.equals(buldiCRPF[i])) {
                          this.desPos = i;
                      }
                      if(startpos!=0&& desPos!=0){
                          route = "buldiCRPF";
                      }
                  }
              }
              else{

              }

        }
    }

    //retuen source index
    int getsourcePosition(){
        return startpos;
    }

    //return destination index
    int getdestinationPosition(){
        return desPos;
    }

    //return route
    String getRoute(){
        return route;
    }

    //return up down
    String upDown(){
        if (startpos > desPos){
            return "down";
        }else{
            return "up";
        }
    }
}
