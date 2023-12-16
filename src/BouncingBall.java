import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

public class BouncingBall extends JFrame {

    ArrayList<Ball> balls = new ArrayList<Ball>();

    Thread t;

	/*
	context switching -> 더블 버퍼링
	Image img_buffer;
	Graphics g_img;
	*/

    public BouncingBall() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1000);
        setLayout(null);

        // 공 생성
        JButton button = new JButton("생성");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // 공 생성을 최대 3개로 제한
                if (balls.size() >= 3) {
                    JOptionPane.showMessageDialog(null, "더 이상 공을 생성할 수 없습니다. ( MAX = 3 )");
                    return;
                }

                balls.add(new Ball());
                add(balls.get(balls.size() - 1));
                balls.get(balls.size() - 1).ballStart();

                try {
                    Thread t = new Thread(balls.get(balls.size() - 1));
                    t.start();
                } catch (Exception e1) {
                }

            } // actionPerformed()
        }); // button.addActionListener()

        // 공 삭제, 범위 밖으로 이동
        JButton button2 = new JButton("삭제");
        button2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
				/*
		        볼 색상을 배경색(238,238,238)로 감추려고 했으나 그냥 좌표를 프레임 범위 밖으로 이동시키면 된다.
				balls.get(deleteCount).c[0] = 238;
				balls.get(deleteCount).c[1] = 238;
				balls.get(deleteCount).c[2] = 238;
				balls.get(deleteCount).c2 = 238;
				*/

                try {
                    balls.get(0).x = -1000;
                    balls.get(0).y = -1000;
                    balls.remove(0);
//					Ball.count -= 1;
                } catch (IndexOutOfBoundsException ie) {
                    JOptionPane.showMessageDialog(null, "더 이상 삭제할 공이 존재하지 않습니다.\n 프로그램을 종료합니다.");
                    System.exit(0);
                }

            } // actionPerformed()
        }); // button2.addActionListener()

        add(button);
        button.setBounds(1680, 900, 100, 50);
        add(button2);
        button2.setBounds(1800, 900, 100, 50);

        setVisible(true);
    } // BouncingBall 생성자

    public static void main(String[] args) {
        new BouncingBall();
    } // main()

    class Ball extends JPanel implements Runnable {
        static final int WIDTH = 1650, HEIGHT = 1000;
//    	static int count;

        int x, y;                    // 공 생성 좌표
        int xInc = 3, yInc = 3;     // 공 이동시킬 값

        int diameter = 200;         // 공 지름

        int[] c = new int[3];       // 공 색상, R = c[0], G = c[1], B = c[2]

        int tX1, tX2, tX3;          // 1,2,3번 공의 x 좌표
        int tY1, tY2, tY3;          // 1,2,3번 공의 y 좌표
        Color color1, color2, color3; // 1,2,3번 공의 색깔

        public Ball() {
            setSize(WIDTH, HEIGHT);

            this.x = (int) (Math.random() * 1350);
            this.y = (int) (Math.random() * 700);

            for (int i = 0; i < c.length; i++)
                c[i] = new Random().nextInt(255);

//    		count++;

            t = new Thread(this);
        } // 생성자

        // 각 공별로 쓰레드 시작
        public void ballStart() {
            t.start();
        } // ballStart()

        @Override
        public void run() {

            while (true) {

                // 공이 프레임 내에서 bouncing 하도록 처리
                if (0 >= x || x > (WIDTH - 250))
                    xInc = -xInc;
                if (0 >= y || y > (HEIGHT - 250))
                    yInc = -yInc;
                x += xInc;
                y += yInc;

                // 공 충돌 처리, 사각형으로 좌표를 설정하고 확인
                for (int i = 0; i < balls.size(); i++) {
                    for (int j = i + 1; j < balls.size(); j++) {

                        if (balls.get(i).x + balls.get(i).diameter >= balls.get(j).x &&
                                balls.get(i).x <= balls.get(j).x + balls.get(i).diameter &&
                                balls.get(i).y + balls.get(i).diameter >= balls.get(j).y &&
                                balls.get(i).y <= balls.get(j).y + balls.get(j).diameter) {

                            if (balls.get(i).xInc == balls.get(j).xInc && balls.get(i).xInc == 3) {
                                if (balls.get(i).x > balls.get(j).x)
                                    balls.get(i).x += 3;
                                else
                                    balls.get(j).x += 3;
                            } else if (balls.get(i).xInc == balls.get(j).xInc && balls.get(i).xInc == -3) {
                                if (balls.get(i).x < balls.get(j).x)
                                    balls.get(i).x -= 3;
                                else
                                    balls.get(j).x -= 3;
                            }

                            balls.get(i).xInc *= -1;
                            balls.get(i).yInc *= -1;
                            balls.get(j).xInc *= -1;
                            balls.get(j).yInc *= -1;

                        } // if
                    } // for
                } // for

                // 공이 2개일 때, 각 공의 중점 좌표와 색깔 저장
                if (balls.size() == 2) {
                    color1 = new Color(balls.get(0).c[0], balls.get(0).c[1], balls.get(0).c[2]);
                    color2 = new Color(balls.get(1).c[0], balls.get(1).c[1], balls.get(1).c[2]);

                    tX1 = balls.get(0).x + 90;
                    tX2 = balls.get(1).x + 90;
                    tY1 = balls.get(0).y + 90;
                    tY2 = balls.get(1).y + 90;
                }

                // 공이 3개일 때, 각 공의 중점 좌표와 색깔 저장
                else if (balls.size() == 3) {
                    color1 = new Color(balls.get(0).c[0], balls.get(0).c[1], balls.get(0).c[2]);
                    color2 = new Color(balls.get(1).c[0], balls.get(1).c[1], balls.get(1).c[2]);
                    color3 = new Color(balls.get(2).c[0], balls.get(2).c[1], balls.get(2).c[2]);

                    tX1 = balls.get(0).x + 90;
                    tX2 = balls.get(1).x + 90;
                    tX3 = balls.get(2).x + 90;
                    tY1 = balls.get(0).y + 90;
                    tY2 = balls.get(1).y + 90;
                    tY3 = balls.get(2).y + 90;
                }

                repaint();

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                }

            } // while
        } // run()

        public void draw(Graphics2D g2) {
            g2.setColor(new Color(c[0], c[1], c[2]));
            g2.fill(new Ellipse2D.Double(x, y, diameter, diameter));
            g2.setColor(new Color(0, 0, 0));
            g2.fillOval(x + 100, y + 100, 10, 10);

            // 공이 2개일 때
            if (balls.size() == 2) {
                int dx = (int) Math.pow((tX2 - tX1), 2);
                int dy = (int) Math.pow((tY2 - tY1), 2);
                int d = (int) Math.sqrt(dx + dy);

                if (tX1 > tX2) {
                    if (tY1 > tY2) {
                        g2.setColor(color2);
                        g2.fillOval(tX1 - d / 25, tY1 - d / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX2 + d / 25, tY2 + d / 25, 25, 25);
                    } else {
                        g2.setColor(color2);
                        g2.fillOval(tX1 - d / 25, tY1 + d / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX2 + d / 25, tY2 - d / 25, 25, 25);
                    }
                } else if (tX1 < tX2) {
                    if (tY1 > tY2) {
                        g2.setColor(color2);
                        g2.fillOval(tX1 + d / 25, tY1 - d / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX2 - d / 25, tY2 + d / 25, 25, 25);
                    } else {
                        g2.setColor(color2);
                        g2.fillOval(tX1 + d / 25, tY1 + d / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX2 - d / 25, tY2 - d / 25, 25, 25);
                    }
                }

                repaint();

            } // if

            // 공이 3개일 때
            if (balls.size() == 3) {
                int dx1 = (int) Math.pow((tX2 - tX1), 2);
                int dy1 = (int) Math.pow((tY2 - tY1), 2);
                int d1 = (int) Math.sqrt(dx1 + dy1);

                int dx2 = (int) Math.pow((tX3 - tX1), 2);
                int dy2 = (int) Math.pow((tY3 - tY1), 2);
                int d2 = (int) Math.sqrt(dx2 + dy2);

                int dx3 = (int) Math.pow((tX3 - tX2), 2);
                int dy3 = (int) Math.pow((tY3 - tY2), 2);
                int d3 = (int) Math.sqrt(dx3 + dy3);

                if (tX1 > tX2) {
                    if (tY1 > tY2) {
                        g2.setColor(color2);
                        g2.fillOval(tX1 - d1 / 25, tY1 - d1 / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX2 + d1 / 25, tY2 + d1 / 25, 25, 25);
                    } else {
                        g2.setColor(color2);
                        g2.fillOval(tX1 - d1 / 25, tY1 + d1 / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX2 + d1 / 25, tY2 - d1 / 25, 25, 25);
                    }
                } else if (tX1 < tX2) {
                    if (tY1 > tY2) {
                        g2.setColor(color2);
                        g2.fillOval(tX1 + d1 / 25, tY1 - d1 / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX2 - d1 / 25, tY2 + d1 / 25, 25, 25);
                    } else {
                        g2.setColor(color2);
                        g2.fillOval(tX1 + d1 / 25, tY1 + d1 / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX2 - d1 / 25, tY2 - d1 / 25, 25, 25);
                    }
                }

                if (tX1 > tX3) {
                    if (tY1 > tY3) {
                        g2.setColor(color3);
                        g2.fillOval(tX1 - d2 / 25, tY1 - d2 / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX3 + d2 / 25, tY3 + d2 / 25, 25, 25);
                    } else {
                        g2.setColor(color3);
                        g2.fillOval(tX1 - d2 / 25, tY1 + d2 / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX3 + d2 / 25, tY3 - d2 / 25, 25, 25);
                    }
                } else if (tX1 < tX3) {
                    if (tY1 > tY3) {
                        g2.setColor(color3);
                        g2.fillOval(tX1 + d2 / 25, tY1 - d2 / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX3 - d2 / 25, tY3 + d2 / 25, 25, 25);
                    } else {
                        g2.setColor(color3);
                        g2.fillOval(tX1 + d2 / 25, tY1 + d2 / 25, 25, 25);
                        g2.setColor(color1);
                        g2.fillOval(tX3 - d2 / 25, tY3 - d2 / 25, 25, 25);
                    }
                }

                if (tX2 > tX3) {
                    if (tY2 > tY3) {
                        g2.setColor(color3);
                        g2.fillOval(tX2 - d3 / 25, tY2 - d3 / 25, 25, 25);
                        g2.setColor(color2);
                        g2.fillOval(tX3 + d3 / 25, tY3 + d3 / 25, 25, 25);
                    } else {
                        g2.setColor(color3);
                        g2.fillOval(tX2 - d3 / 25, tY2 + d3 / 25, 25, 25);
                        g2.setColor(color2);
                        g2.fillOval(tX3 + d3 / 25, tY3 - d3 / 25, 25, 25);
                    }
                } else if (tX2 < tX3) {
                    if (tY2 > tY3) {
                        g2.setColor(color3);
                        g2.fillOval(tX2 + d3 / 25, tY2 - d3 / 25, 25, 25);
                        g2.setColor(color2);
                        g2.fillOval(tX3 - d3 / 25, tY3 + d3 / 25, 25, 25);
                    } else {
                        g2.setColor(color3);
                        g2.fillOval(tX2 + d3 / 25, tY2 + d3 / 25, 25, 25);
                        g2.setColor(color2);
                        g2.fillOval(tX3 - d3 / 25, tY3 - d3 / 25, 25, 25);
                    }
                }
            }

            repaint();

        } // draw()

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            super.paintComponent(g2);

            for (Ball a : balls)
                a.draw(g2);
        } // paintComponent()

    	/* 더블 버퍼링으로 쓰레드 context switching 인한 깜빡거림을 제거하려고 했으나 다른 방법으로 처리.
    	@Override
    	public void update(Graphics g) {
    		paint(g);
    	}
    	@Override
    	public void paint(Graphics g) {
    		if(img_buffer == null) {
    			img_buffer = createImage(1500,1000);
    			g_img = img_buffer.getGraphics();
    		}

			paintComponent(g_img);

			g_img.setColor(new Color(c[0],c[1],c[2]));
			g_img.fillOval(x,y,diameter,diameter);


    		g.drawImage(img_buffer, 0, 0, null);
    	}
    	*/

    } // Inner Class
} // Outer class

