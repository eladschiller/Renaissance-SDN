require(vioplot)
dev.new(width=850, height=650)
b4 <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/messagesToStab/b4.txt")
clos <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/messagesToStab/clos.txt")
telstra <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/messagesToStab/telstra.txt")
att <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/messagesToStab/att.txt")
ebone <- scan("~/SDN/renaissance/Evaluation/ivanandantonPlots/messagesToStab/ebone.txt")
A <- c(b4)
B <- c(clos)
C <- c(telstra)
D <- c(att)
E <- c(ebone)
boxplot(A,B,C,D,E, col="gray", log = "y", axes = FALSE, ann = FALSE)
axis(side=1,at=1:5,labels=c("B4", "Clos","Telstra","AT&T","EBONE"), cex.axis=1.5)
labl <- rep("", 32)
labl[1] <- "1"
labl[10] <- "10"
labl[19] <- "100"
labl[20] <- "200"
labl[21] <- "300"
labl[22] <- "400"
labl[23] <- "500"
labl[24] <- "600"
labl[25] <- "700"
labl[26] <- "800"
labl[27] <- "900"
labl[28] <- "1000"
labl[30] <- "2000"
labl[31] <- "3000"
labl[32] <- "4000"
op <- par(mar = c(6,6,4,2) + 0.2)
nums <- c(1:10, seq(20, 100, 10), seq(200, 1000, 100), seq(1000, 4000, 1000))
axis(2, at = nums, label = labl, las = 2, cex.axis=1.5)
title(font.lab = 1, cex.lab=2, ylab="Number of messages", line=4)
title(font.lab = 1, cex.lab=2, xlab="Network", line=4)
box()
par(op)