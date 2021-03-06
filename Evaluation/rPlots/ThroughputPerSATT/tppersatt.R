require(vioplot)
dev.new(width=850, height=650)
s1 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s1.txt")
s2 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s2.txt")
s3 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s3.txt")
s4 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s4.txt")
s5 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s5.txt")
s6 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s6.txt")
s7 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s7.txt")
s8 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s8.txt")
s9 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s9.txt")
s10 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s10.txt")
s11 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s11.txt")
s12 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s12.txt")
s13 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s13.txt")
s14 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s14.txt")
s15 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s15.txt")
s16 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s16.txt")
s17 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s17.txt")
s18 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s18.txt")
s19 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s19.txt")
s20 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s20.txt")
s21 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s21.txt")
s22 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s22.txt")
s23 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s23.txt")
s24 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s24.txt")
s25 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s25.txt")
s26 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s26.txt")
s27 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s27.txt")
s28 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s28.txt")
s29 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s29.txt")
s30 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/ThroughputPerSATT/s30.txt")
A <- c(s1)
B <- c(s2)
C <- c(s3)
D <- c(s4)
E <- c(s5)
F <- c(s6)
G <- c(s7)
H <- c(s8)
I <- c(s9)
J <- c(s10)
K <- c(s11)
L <- c(s12)
M <- c(s13)
N <- c(s14)
O <- c(s15)
A1 <- c(s16)
B1 <- c(s17)
C1 <- c(s18)
D1 <- c(s19)
E1 <- c(s20)
F1 <- c(s21)
G1 <- c(s22)
H1 <- c(s23)
I1 <- c(s24)
J1 <- c(s25)
K1 <- c(s26)
L1 <- c(s27)
M1 <- c(s28)
N1 <- c(s29)
O1 <- c(s30)
op <- par(mar = c(6,6,4,2) + 0.2)
plot(0:1,0:1,type="n",xlim=c(1,30),ylim=c(400,525),axes=FALSE,ann=FALSE)
vioplot(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,A1,B1,C1,D1,E1,F1,G1,H1,I1,J1,K1,L1,M1,N1,O1, col="gray", add=TRUE)
title(font.lab = 1, cex.lab=2, ylab="Throughput (MBits/s)", xlab="Time (Seconds)", line=3.6)
axis(side=1,at=1:30,labels=c("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30"), cex.axis=1.5)
axis(2, at = seq(400,525, by = 25), las=2, cex.axis=1.5)
par(op)