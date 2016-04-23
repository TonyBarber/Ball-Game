package com.example.jkwusu.surrondtheball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.graphics.Paint;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by jkwusu on 2016/4/21.
 */
public class StartToPlay extends SurfaceView implements View.OnTouchListener{

    private static int WIDTH=40;
    private static final int ROW=10;
    private static final int COL=10;
    private static final int BLOCK=10;

    Dot matrix[][]=new Dot[ROW][COL];
    private Dot keydot;

    public StartToPlay(Context context) {
        super(context);
        getHolder().addCallback(callback);
        matrix=new Dot[ROW][COL];

        for (int i=0;i<ROW;i++){
            for (int j=0;j<COL;j++){
                matrix[i][j]=new Dot(j,i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }

    private void draw(){
        Canvas canvas=getHolder().lockCanvas();
        canvas.drawColor(Color.LTGRAY);
        Paint paint=new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for (int i=0;i<ROW;i++){
            int offset=0;
            if (i%2==0){
                offset=WIDTH/2;
            }
            for (int j=0;j<COL;j++){
                Dot one=getDot(j,i);
                switch (one.getStatus()){
                    case Dot.STATUS_OFF:
                        paint.setColor(Color.GRAY);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(Color.BLACK);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(Color.RED);
                        break;
                    default:
                        break;
                }
                canvas.drawOval(new RectF(one.getX()*WIDTH+offset,one.getY()*WIDTH,
                        (one.getX()+1)*WIDTH+offset,(one.getY()+1)*WIDTH),paint);
            }
        }

        getHolder().unlockCanvasAndPost(canvas);
    }

    Callback callback=new Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            draw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            WIDTH=width/(COL+1);
            draw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private void initGame(){
        for (int i=0;i<ROW;i++){
            for (int j=0;j<COL;j++){
                matrix[i][j].setStatus(Dot.STATUS_OFF);
            }
        }
        keydot=new Dot(4,5);
        getDot(4,5).setStatus(Dot.STATUS_IN);
        for (int i=0;i<BLOCK;){
            int x= (int) (Math.random()*1000%COL);
            int y= (int) (Math.random()*1000%ROW);
            if (getDot(x,y).getStatus()==Dot.STATUS_OFF){
                getDot(x,y).setStatus(Dot.STATUS_ON);
                i++;
            }
        }
    }

    private Dot getDot(int x,int y){
        return matrix[y][x];
    }

    private boolean isAtEdge(Dot d){
        if (d.getX()*d.getY()==0||d.getX()+1==COL||d.getY()+1==ROW){
            return true;
        }
        return false;
    }

    private Dot getNeighbour(Dot d,int dir){
        switch (dir){
            case 1:
                return getDot(d.getX()-1,d.getY());
            case 2:
                if (d.getY()%2==0){
                    return getDot(d.getX(),d.getY()-1);
                }else {
                    return getDot(d.getX()-1,d.getY()-1);
                }
            case 3:
                if (d.getY()%2==0){
                    return getDot(d.getX()+1,d.getY()-1);
                }else {
                    return getDot(d.getX(),d.getY()-1);
                }
            case 4:
                return getDot(d.getX()+1,d.getY());
            case 5:
                if (d.getY()%2==0){
                    return getDot(d.getX()+1,d.getY()+1);
                }else {
                    return getDot(d.getX(),d.getY()+1);
                }
            case 6:
                if (d.getY()%2==0){
                    return getDot(d.getX(),d.getY()+1);
                }else {
                    return getDot(d.getX()-1,d.getY()+1);
                }
            default:
                return null;
        }
    }

    private int getDistance(Dot d,int dir){
        int distance=0;
        if (isAtEdge(d)){
            return 1;
        }
        Dot ori=d,next;
        while (true){
            next=getNeighbour(ori,dir);
            if (next.getStatus()==Dot.STATUS_ON){
                return distance*-1;
            }
            if (isAtEdge(next)){
                distance++;
                return distance;
            }
            distance++;
            ori=next;
        }
    }

    private void MoveTo(Dot d){
        d.setStatus(Dot.STATUS_IN);
        getDot(keydot.getX(),keydot.getY()).setStatus(Dot.STATUS_OFF);
        keydot.setXY(d.getX(),d.getY());
    }

    private void move(){
        if (isAtEdge(keydot)){
            lose();
            return;
        }
        Vector<Dot> avaliable=new Vector<>();
        Vector<Dot> positive=new Vector<>();
        HashMap<Dot,Integer> al=new HashMap<>();
        for (int i=1;i<7;i++){
            Dot n=getNeighbour(keydot,i);
            if (n.getStatus()==Dot.STATUS_OFF){
                avaliable.add(n);
                al.put(n,i);
                if (getDistance(n,i)>0){
                    positive.add(n);
                }
            }
        }
        if (avaliable.size()==0){
            win();
        }else if (avaliable.size()==1){
            MoveTo(avaliable.get(0));
        }else {
            Dot best=null;
            if (positive.size()!=0){
                int min=12;
                for (int i=0;i<positive.size();i++){//存在出路
                    int a=getDistance(positive.get(i),al.get(positive.get(i)));
                    if (a<min){
                        min=a;
                        best=positive.get(i);
                    }
                }
            }else {//所有方向都被堵住
                int max=0;
                for (int i=0;i<avaliable.size();i++){
                    int k=getDistance(avaliable.get(i),al.get(avaliable.get(i)));
                    if (k<max){
                        max=k;
                        best=avaliable.get(i);
                    }
                }
            }
            MoveTo(best);
        }
    }

    private void lose(){
        Toast.makeText(getContext(),"Lose~~~~",Toast.LENGTH_SHORT).show();
    }

    private void win(){
        Toast.makeText(getContext(),"Win~~~~",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_UP){
            int x,y;
            y= (int) (event.getY()/WIDTH);
            if (y%2==0){
                x= (int) ((event.getX()-WIDTH/2)/WIDTH);
            }else {
                x= (int) (event.getX()/WIDTH);
            }
            if (x+1>COL||y+1>ROW){
                initGame();
            }else if (getDot(x,y).getStatus()==Dot.STATUS_OFF){
                getDot(x,y).setStatus(Dot.STATUS_ON);
                move();
            }
            draw();
        }
        return true;
    }
}
