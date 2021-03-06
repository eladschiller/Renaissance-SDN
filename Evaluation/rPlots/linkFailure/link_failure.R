require(vioplot)
dev.new(width=850, height=650)
b4 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/linkFailure/b4.txt")
clos <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/linkFailure/clos.txt")
telstra <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/linkFailure/telstra.txt")
att <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/linkFailure/att.txt")
ebone <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/linkFailure/ebone.txt")
A <- c(b4)
B <- c(clos)
C <- c(telstra)
D <- c(att)
E <- c(ebone)
op <- par(mar = c(6,6,4,2) + 0.2)
plot(0:1,0:1,type="n",xlim=c(0.5,5.5),ylim=c(0,18),axes=FALSE,ann=FALSE)
vioplot(A,B,C,D,E, col="gray", add=TRUE)
title(font.lab = 1, cex.lab=2, ylab="Time (seconds)", xlab="Network")
axis(side=1,at=1:5,labels=c("B4", "Clos","Telstra","AT&T","EBONE"), cex.axis=1.5)
axis(2, at = seq(0,18, by = 2), las=2, cex.axis=1.5)
par(op)
