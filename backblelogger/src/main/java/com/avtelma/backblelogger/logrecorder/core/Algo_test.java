package com.avtelma.backblelogger.logrecorder.core;


/*
Tasks for nearby future:
- ProcessRawValue = method that returns "Value" type from accelerometers containing all the data we need depending on Sensor type created
- Think about refactoring for analyze big chunks of data
- Think about reimplementing azimuting procedure, with checking both of gas\breaks and locating the event
when both of those "vectors" will be on the same line
- Add locating of vertical bumps as it's implemented in MATLAB version of Algorithm
 */

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;
//Connection points:
//DataProcessor.java - for logging events
//??? - for Data acquisition

public class Algo_test
{
    Vector<Integer> Events; //Array of current located events
    int TS; //Timestamp

    public static double STOP_TRESHHOLD=0.02; //constant for stop threshold, made as variable for future improvements
    public static double GAS_TRESHHOLD=0.1; //constant for gas\breaks treshhold made as variable for future improvements
    public static double TURN_TRESHHOLD=0.1; //constant for right\left turn threshholds made as variable for future improvements
    public static int Chunk_size=10;//Variable that contains the number of records in second for 1 Chunk
    Vector<Vector<Double>> Chunk; //Current chunk of accelerometers data
    Vector<Vector<Double>> Chunk_SSK; //Data Chunk transformed to body-frame
    Vector<Double> Grav; //Gravity vector (in format of 3D vector with 2 zeros and 1g. Which component will be non-zero is determined later)
    Vector<Double> Acc_one;//Uni-vectror of acceleration to determine forward location in the future
    Vector<Double> Q_Hor; //Orientation quaternions between IMU-frame and vehicle body-frame
    Vector<Double> Q_Forw;
    Vector<Double> Q;
    Vector<Double> Suspect;
    Vector<Double> A_raw;
    int Hor_counter=0;
    //float Q[];
    int First_Chunk_counter=0;
    //Events_logger Logger;
    int Chunk_pos=0; //current position for Data_Chunk;
    //Double DevA[];//Standard deviation and module (4-dimension vector, 3 axis+modulus)
    //Vector<Double> MeanA;//Math expected Value;
    float T_suspect;//Time of last gas\break located for Azimuting
    int Vert; //Vertical Channel index
    int Forw; //Front channel index
    int Hor_chan[];//2 Horizontal channels indexes
    int Side;

    public String log_string;
    public String FName;
    //boolean
    long Timestamp;

    boolean isLocatedVert=false;
    boolean isLocatedForw=false;
    int isHorizonted=0; //Flag for Horizonting procedure
    int isAzimuted=0; //Flag for Azimuting procedure

    public Algo_test() {
        Calendar calender1 = Calendar.getInstance();
        long FName_seconds = calender1.getTimeInMillis();
        this.FName = "EVENTS" + Long.toString(FName_seconds) + ".txt";
        Chunk = new Vector<>();
        Chunk_SSK = new Vector<>();
        Grav = new Vector<>();
        Q_Hor=new Vector<>();
        Q_Forw=new Vector<>();
        Q = new Vector<>();
        Suspect=new Vector<>();
        Acc_one=new Vector<>();
        A_raw=new Vector<>();
        for (int i=0;i<4;i++)
        {
            Q_Hor.add(0.0);
            Q_Forw.add(0.0);
            Q.add(0.0);
        }
        Q_Hor.set(0,1.0);
        Q_Forw.set(0,1.0);
        Q.set(0,1.0);
        for (int i = 0; i < 3; i++)
        {
            Grav.add(0.0);
            Suspect.add(0.0);
            Acc_one.add(0.0);
            A_raw.add(0.0);
        }
        for(int i=0;i<Chunk_size;i++)
        {
            Vector<Double> row=new Vector<>();
            for(int j=0;j<3;j++)
            {
                row.add(0.0);
            }
            Chunk.add(row);
            Chunk_SSK.add(row);
        }
        //Vector<Integer>Events=new Vector<>();
        Events=new Vector<>();
        for (int i=0;i<6;i++)
        {
            this.Events.add(0);
        }
        //Oh, this is gonna hurt sometimes later
        //Logger.configure(true);
    }
    public void Log_data()
    {
//        FileOutputStream Fout=openFileOutput;
//        OutputStreamWriter osw=new OutputStreamWriter(Fout);
        try {
            File Events_file = new File(Environment.getExternalStorageDirectory(),FName);
            if (!Events_file.exists()) {
                Events_file.createNewFile();
            }
            FileWriter writer = new FileWriter(Events_file,true);
            writer.append(this.log_string+"\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {}

        //Logger.info("test");
    }
    public  void DataGain(float Ax, float Ay, float Az)
    {
        Vector<Double> Temp_vec=new Vector<>();
        Temp_vec.add((double) Ax);
        Temp_vec.add((double) Ay);
        Temp_vec.add((double) Az);
        A_raw=Temp_vec;
        Chunk.set(Chunk_pos,Temp_vec);
        Chunk_pos++;
        if(Chunk_pos>=Chunk_size) {Chunk_pos=0;First_Chunk_counter++;}
        for(int i=0;i<this.Events.size();i++)
        {
            this.Events.set(i,0);
        }
    }

    //Find_vert_index returns the index of maximum (by modulus) element in the vector
    //Method is used for finding the directions of "g" and acceleration.
    public int Find_vert_index(Vector<Double> Vec_Data)
    {
        int res=0;
        for(int i=0;i<Vec_Data.size();i++)
        {
            if(Math.abs(Vec_Data.get(i))>= Math.abs(Vec_Data.get(res))){res=i;}
        }
        return res;
    }
    public Vector<Double> Mean(Vector<Vector<Double>> Data_vec)
    {
        Vector<Double> Sum=new Vector<>();
        Vector<Double> Mean_res=new Vector<>();
        for(int k=0;k<Data_vec.get(0).size();k++)
        {
            Sum.add(0.0);
            //Sum.set(k,0.0);
        }

        for(int i=0;i<Data_vec.size();i++)
        {
            for(int k=0;k<Data_vec.get(0).size();k++)
            {
                Sum.set(k, Sum.get(k) + Data_vec.get(i).get(k));
            }
        }
        for (int j=0;j<Data_vec.get(0).size();j++)
        {
            Mean_res.add(Sum.get(j) / Data_vec.size());
        }
        return Mean_res;
    }

    public Vector<Double> Deviation(Vector<Vector<Double>> Data_vec, Vector<Double> Data_Mean)
    {
        Vector<Double> Temp=new Vector<>();
        Vector<Double> Div_res=new Vector<>();
        for(int k=0;k<Data_vec.get(0).size();k++)
        {
            //Temp.set(k,0.0);
            Temp.add(0.0);
        }
        for(int i=0;i<Data_vec.size();i++)
        {
            for(int k=0;k<Data_vec.get(0).size();k++)
            {
                Temp.set(k,Temp.get(k)+ Math.pow((Data_Mean.get(k)-Data_vec.get(i).get(k)),2.0));
            }
        }
        for (int j=0;j<Data_vec.get(0).size();j++)
        {
            Div_res.add(Math.sqrt(Temp.get(j) / Data_vec.size()));
        }
        return Div_res;


    }

    public double VectorNorm(Vector<Double> Inc_Vec)
    {
        double result=0.0;
        double temp=0.0;
        for(int i=0;i<Inc_Vec.size();i++)
        {
            temp+= Math.pow(Inc_Vec.get(i),2.0);
        }
        //result=temp/Inc_Vec.size();
        result= Math.sqrt(temp);
        return result;
    }

    public double VectorNorm(double x,double y,double z)
    {
        double result;
        result= Math.sqrt(Math.pow(x,2.0)+ Math.pow(y,2.0)+ Math.pow(z,2.0));
        return result;
    }
    public Vector<Double> NormVector(Vector<Double> Inc_vec)
    {
        double norm;
        Vector<Double> result=new Vector<>();
        norm=VectorNorm(Inc_vec);
        if(norm==0)
        {
            for(int i=0;i<Inc_vec.size();i++)
            {
                result.add(0.0);
            }

        }
        else
        {
            for (int i = 0; i < Inc_vec.size(); i++) {
                result.add(Inc_vec.get(i) / norm);
            }
        }
        return result;
    }
    public double DotProd(Vector<Double> V1, Vector<Double> V2)
    {
        double dotprod=0.0;
        if(V1.size()!=V2.size()){return 0.0;}
        for(int i=0;i<V1.size();i++)
        {
            dotprod+=(V1.get(i))*(V2.get(i));
        }
        return dotprod;

    }
    public Vector<Double> CrossProd(Vector<Double> V1, Vector<Double> V2)
    {
        Vector<Double> CrossRes=new Vector<>();
        if((V1.size()!=3)&&V2.size()!=3){CrossRes.add(0.0);CrossRes.add(0.0);CrossRes.add(0.0);return CrossRes;};
        CrossRes.add(V1.get(1)*V2.get(2)-V1.get(2)*V2.get(1));
        CrossRes.add(V1.get(2)*V2.get(0)-V1.get(0)*V2.get(2));
        CrossRes.add(V1.get(0)*V2.get(1)-V1.get(1)*V2.get(0));
        return CrossRes;
    }
    public Vector<Double> RotVecCalc(Vector<Double> V1, Vector<Double> V2)
    {
        //Dont forget to normalize vectors before using this method, otherwise, results will be unpredictable
        Vector<Double> RotVec=new Vector<>();
        //Vector<Double> Ax=new Vector<>();
        double Ang=0.0;
        double cosang=0.0;
        cosang=DotProd(V1,V2)/(VectorNorm(V1)*VectorNorm(V2));
        RotVec=CrossProd(V1,V2);
        Ang= Math.acos(cosang);
        RotVec.add(Ang);
        //RotVec.add(DotProd(V1,V2));
        return RotVec;
    }
    public Vector<Double> QuatDivide(Vector<Double> q, Vector<Double> r)
    {
        Vector<Double> t=new Vector<>();
        t.add((r.get(0)*q.get(0)+r.get(1)*q.get(1)+r.get(2)*q.get(2)+r.get(3)*q.get(3))/VectorNorm(r));
        t.add((r.get(0)*q.get(1)-r.get(1)*q.get(0)-r.get(2)*q.get(3)+r.get(3)*q.get(2))/VectorNorm(r));
        t.add((r.get(0)*q.get(2)+r.get(1)*q.get(3)-r.get(2)*q.get(0)-r.get(3)*q.get(1))/VectorNorm(r));
        t.add((r.get(0)*q.get(3)-r.get(1)*q.get(2)+r.get(2)*q.get(1)-r.get(3)*q.get(0))/VectorNorm(r));
        return t;
    }
    public Vector<Double> Axang2Q(Vector<Double> axang)
    {
        Vector<Double> Q_return=new Vector<>();
        double v1,v2,v3,norm;
        double ThetaHalf;
        ThetaHalf=axang.get(3)/2;
        norm=VectorNorm(axang.get(0),axang.get(1),axang.get(2));
        v1=axang.get(0)/norm;
        v2=axang.get(1)/norm;
        v3=axang.get(2)/norm;
        Q_return.add(Math.cos(ThetaHalf));
        Q_return.add(v1* Math.sin(ThetaHalf));
        Q_return.add(v2* Math.sin(ThetaHalf));
        Q_return.add(v3* Math.sin(ThetaHalf));
        return Q_return;


    }
    public double AngleBetweenQ(Vector<Double> Q1, Vector<Double> Q2)
    {
        Vector<Double> t=new Vector<>();
        double Theta=0.0;
        t=QuatDivide(Q1,Q2);
        Theta= Math.acos(t.get(0))*2;
        return Theta;

    }

    public double rad2deg(double ang_rad)
    {
        double ang_deg;
        ang_deg=(180/ Math.PI)*ang_rad;
        return ang_deg;
    }

    public double deg2rad(double ang_deg)
    {
        double ang_rad;
        ang_rad=(Math.PI/180)*ang_deg;
        return ang_rad;
    }

    //Need to change timestamp on .apk
    public Vector<Double> Quat2DCM(Vector<Double> Q)
    {
        Vector<Double> DCM = new Vector<>();
        Q=NormVector(Q);
        DCM.add(Math.pow(Q.get(0),2.0)+ Math.pow(Q.get(1),2.0)- Math.pow(Q.get(2),2.0)- Math.pow(Q.get(3),2.0));
        DCM.add(2*(Q.get(1)*Q.get(2)+Q.get(0)*Q.get(3)));
        DCM.add(2*(Q.get(1)*Q.get(3)-Q.get(0)*Q.get(2)));
        DCM.add(2*(Q.get(1)*Q.get(2)-Q.get(0)*Q.get(3)));
        DCM.add(Math.pow(Q.get(0),2.0)- Math.pow(Q.get(1),2.0)+ Math.pow(Q.get(2),2.0)- Math.pow(Q.get(3),2.0));
        DCM.add(2*(Q.get(2)*Q.get(3)+Q.get(0)*Q.get(1)));
        DCM.add(2*(Q.get(1)*Q.get(3)+Q.get(0)*Q.get(2)));
        DCM.add(2*(Q.get(2)*Q.get(3)-Q.get(0)*Q.get(1)));
        DCM.add(Math.pow(Q.get(0),2.0)- Math.pow(Q.get(1),2.0)- Math.pow(Q.get(2),2.0)+ Math.pow(Q.get(3),2.0));
        return DCM;


    }
    public Vector<Double> InvVector(Vector<Double> vec)
    {
        Vector<Double> inv_vec=new Vector<>();
        for (int i=0;i<vec.size();i++)
        {
            inv_vec.add(-vec.get(i));
        }
        return inv_vec;
    }
    public Vector<Double> Quatrotate(Vector<Double> Q, Vector<Double> vec)
    {
        Vector<Double> vec_rot=new Vector<>();
        Vector<Double> DCM=new Vector<>();
        DCM=Quat2DCM(Q);
        vec_rot.add(DCM.get(0)*vec.get(0)+DCM.get(1)*vec.get(1)+DCM.get(2)*vec.get(2));
        vec_rot.add(DCM.get(3)*vec.get(0)+DCM.get(4)*vec.get(1)+DCM.get(5)*vec.get(2));
        vec_rot.add(DCM.get(6)*vec.get(0)+DCM.get(7)*vec.get(1)+DCM.get(8)*vec.get(2));
        return vec_rot;

    }
    public void Work_Cycle()
    {
        Calendar calender1 = Calendar.getInstance();

        log_string="";
        log_string+= Long.toString(calender1.getTimeInMillis())+";\t";
        int condition=0;
        Vector<Double> Out_Mean=new Vector<>();
        Vector<Double> Mean_full=new Vector<>();
        Vector<Double> Dev_full=new Vector<>();
        Mean_full=Mean(Chunk);
        Dev_full=Deviation(Chunk,Mean_full);
        Chunk_SSK=Chunk;
        double Dev_full_mod= Math.sqrt(Math.pow(Dev_full.get(0),2.0)+ Math.pow(Dev_full.get(1),2.0)+ Math.pow(Dev_full.get(2),2.0));
        condition=isHorizonted+isAzimuted;
        for(int i=0;i<Events.size();i++)
        {
            Events.set(i,0);
        }
        if(First_Chunk_counter==0){condition=-1;}
        Events.set(5,condition);
        switch (condition)
        {
            case 0:
                Out_Mean=Mean(Chunk_SSK);
                //log_string+=String.format("8.2f %8.2f %8.2f ", Chunk_SSK.get(0),Chunk_SSK.get(1),Chunk_SSK.get(2));
                log_string+= String.format("%8.2f;\t %8.2f;\t %8.2f;\t", Out_Mean.get(0),Out_Mean.get(1),Out_Mean.get(2));
                //String.format("%8.2f%4$s %8.2f%4$s %8.2f%4$s", value.getX(), value.getY(), value.getZ(), getLogValueUnit());

                Vector<Double> Norm_Mean=new Vector<>(); //Normalized mean vector
                Norm_Mean=NormVector(Mean_full); //Normalize mean vector for correct math work
                if (Math.abs(Dev_full_mod)<STOP_TRESHHOLD) //Magic-number stop criteria
                {
                    this.Events.set(0,1); //Stop located
                    if(!isLocatedVert)
                    {
                        Vert=Find_vert_index(Mean_full);
                        Grav.set(Vert,1.0);
                        isLocatedVert=true;

                    }
                    Vector<Double> Axang_Hor=new Vector<>();
                    Axang_Hor=RotVecCalc(Grav,Norm_Mean);
                    Vector<Double> Q_Hor_old=new Vector<>();
                    Q_Hor_old=Q_Hor;
                    Q_Hor=Axang2Q(Axang_Hor);
                    if(rad2deg(AngleBetweenQ(Q_Hor,Q_Hor_old))<5.0){this.Hor_counter++;}
                    if(this.Hor_counter>30){this.isHorizonted=1;}
                    //log_string+=String.format("%8.2f\t %8.2f\t %8.2f\t %8.2f\t",Q_Hor.get(0),Q_Hor.get(1),Q_Hor.get(2),Q_Hor.get(3));
                    //log_string+=String.format("%8.2f\t %8.2f\t %8.2f\t %8.2f\t",Axang_Hor.get(0),Axang_Hor.get(1),Axang_Hor.get(2),Axang_Hor.get(3));
                    //log_string+=String.format("%8.2f\t",rad2deg(AngleBetweenQ(Q_Hor,Q_Hor_old)));

                }
                for(int i=0;i< this.Events.size();i++)
                {
                    log_string+= String.format("%d;\t",Events.get(i));
                }
                //Log_data();
                break;
            case 1:
                if (Math.abs(Dev_full_mod)<STOP_TRESHHOLD) //Magic-number stop criteria
                {
                    this.Events.set(0,1); //Stop located
                }
                Out_Mean=Quatrotate(Q_Hor,Mean_full);
                log_string+= String.format("%8.2f;\t %8.2f;\t %8.2f;\t", Out_Mean.get(0),Out_Mean.get(1),Out_Mean.get(2));
                Out_Mean.set(Vert,0.0);
                if(VectorNorm(Out_Mean)>0.175)
                {
                    this.Suspect = InvVector(Out_Mean); //We're inverting vectors for suspect, because we're planning to use "Breaks" moment
                    // to determine the forward position
                    if (!isLocatedForw) {

                        Forw = Find_vert_index(Out_Mean);
                        Acc_one.set(Forw, 1.0);
                        isLocatedForw = true;
                        Vector<Double> Side_helper=new Vector<>();
                        for(int k=0;k<3;k++)
                        {
                            Side_helper.add(1.0);
                        }
                        Side_helper.set(Vert,0.0);
                        Side_helper.set(Forw,0.0);
                        Side=Find_vert_index(Side_helper);
                    }
                }
                if((this.Events.get(0)==1)&&isLocatedForw)
                {
                    Vector<Double> Axang_forw=new Vector<>();
                    Axang_forw=RotVecCalc(Acc_one,Out_Mean);
                    Vector<Double> Q_forw_old=Q_Forw;
                    Q_Forw=Axang2Q(Axang_forw);
                    if(rad2deg(AngleBetweenQ(Q_Forw,Q_forw_old))<5){this.isAzimuted=1;}

                }
                for(int i=0;i< this.Events.size();i++)
                {
                    log_string+= String.format("%d;\t",Events.get(i));
                }
                //Log_data();
                break;


            case 2:
                if (Math.abs(Dev_full_mod)<STOP_TRESHHOLD) //Magic-number stop criteria
                {
                    this.Events.set(0,1); //Stop located
                }
                Vector<Double> Dev_SSK=new Vector<>();
                Out_Mean=Quatrotate(Q_Hor,Mean_full);
                Out_Mean=Quatrotate(Q_Forw,Out_Mean);
                log_string+= String.format("%8.2f;\t %8.2f;\t %8.2f;\t", Out_Mean.get(0),Out_Mean.get(1),Out_Mean.get(2));
                Dev_SSK=Quatrotate(Q_Hor,Dev_full);
                Dev_SSK=Quatrotate(Q_Forw,Dev_SSK);

                //No sign check whatsoever

                if(Math.abs(Out_Mean.get(Forw))>0.15)
                {
                    Events.set(1,(int) Math.signum(Out_Mean.get(Forw)));
                }

                if(Math.abs(Out_Mean.get(Side))>0.1)
                {
                    Events.set(2,(int) Math.signum(Out_Mean.get(Side)));
                }

                if(Dev_SSK.get(Vert)>0.15)
                {
                    Events.set(3,1);
                }

                for(int i=0;i< this.Events.size();i++)
                {
                    log_string+= String.format("%d;\t",Events.get(i));
                }


                //Log_data();
                break;
            default:
                Out_Mean=Mean(Chunk_SSK);
                log_string+= String.format("%8.2f;\t %8.2f;\t %8.2f;\t", Out_Mean.get(0),Out_Mean.get(1),Out_Mean.get(2));
                for(int i=0;i< this.Events.size();i++)
                {
                    log_string+= String.format("%d;\t",Events.get(i));
                }
                //Log_data();
                break;
        }
        log_string+= String.format("%8.2f;\t %8.2f;\t %8.2f;\t", A_raw.get(0),A_raw.get(1),A_raw.get(2));
        Log_data();
    }
//    public void LogEvents(String events_log, Context context);
//    {
//        OutputStreamWriter EventLogger=new OutputStreamWriter(context.openFileOutput(this.FName,context.MODE_PRIVATE));
//
//    }
}