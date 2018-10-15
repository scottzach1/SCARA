# -*- coding: utf-8 -*-
"""
Created on Fri Sep 21 10:29:25 2018

@author: arthur
"""
import numpy as np
import matplotlib as ml
import matplotlib.pyplot as plt


xm1 = 239
ym1 = 480 
xm2 = 375
ym2 = 480 
R = 290
theta1 = 130 * np.pi/180
theta2 = 58 * np.pi/180

xj1 = xm1 + R*np.cos(theta1)
yj1 = ym1 - R*np.sin(theta1)
xj2 = xm2 + R*np.cos(theta2)
yj2 = ym2 - R*np.sin(theta2)

xa = (xj1+xj2)/2
ya = (yj1+yj2)/2
dx = xj2 - xj1
dy = yj2 - yj1
d = np.sqrt(dx*dx+dy*dy)
h = np.sqrt(R*R-(d/2)*(d/2))
alpha = np.arctan2(yj2-yj1,xj2-xj1)


xt1 = xa + h*np.cos(alpha-np.pi/2)
yt1 = ya + h*np.sin(alpha-np.pi/2)
xt2 = xa - h*np.cos(alpha-np.pi/2)
yt2 = ya - h*np.sin(alpha-np.pi/2)

#Xt,Yt=np.meshgrid(xt,yt)
#dx1 = Xt - xm1
#dy1 = Yt - ym1
#alpha1 = np.pi - np.arctan2(dy1,dx1)
#xa1 = xm1 + dx1/2
#ya1 = ym1 + dy1 /2
#d1 = np.sqrt(dx1*dx1 + dy1*dy1)
#h1 = np.sqrt(R*R - (d1/2)*(d1/2))
#xj1 = xa1 - h1*np.cos(np.pi/2 - alpha1)
#yj1 = ya1 - h1*np.sin(np.pi/2 - alpha1)

#theta1 = np.arctan2(yj1-ym1,xj1-xm1)*180/np.pi

fig = plt.figure()
plt.plot([xm1,xj1,xt1],[ym1,yj1,yt1])
plt.plot([xm2,xj2,xt1],[ym2,yj2,yt1])

plt.axis([0,640,0,480])
ax = plt.gca()
ax.set_autoscale_on(False)
plt.gca().invert_yaxis()

#ax = fig.add_subplot(111,projection='3d')
#im = plt.pcolormesh(X,Y,SIG, cmap='hot')
#ax.plot_surface(Xt,Yt,theta1)
plt.show() 
