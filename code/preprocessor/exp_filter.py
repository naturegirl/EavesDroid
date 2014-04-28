import os
import matplotlib.pyplot as plt

def load_csv(filename):
	ts = []
	gf = []
	df = open(filename, "r")
	header = df.readline()
	for line in df:
		sa = line.split(",")
		ts.append(int(sa[0]))
		gf.append(float(sa[1]))
	return ts, gf

def exp_smooth(data, alpha):
	output = [data[0]]
	for i in range(1,len(data)):
		output.append(output[i-1] + alpha * (data[i] - output[i-1]))
	return output

def snip_ends(ts, gf):
	start = ts[0]
	for i in range(1, len(ts)):
		if ts[i] >= start + 150000:
			break;
	si = i
	end = ts[len(ts)-1]
	for i in range(1, len(ts)):
		if ts[i] >= end - 150000:
			break;
	ei = i
	return ts[si:ei],gf[si:ei]

def process_dir(path, outdir, smooth=True):
	HEADER = "timestamp, gforce\n"
	proc_path = outdir
	if not os.path.exists(proc_path):
		try:
			os.mkdir(proc_path)
		except OSError as error:
			print error
	for filename in os.listdir(path):
		file_path = os.path.join(path, filename)
		if os.path.isdir(file_path):
			continue;
		ts,gf = load_csv(file_path)
		ts,gf = snip_ends(ts, gf)
		if smooth:
			gf = exp_smooth(gf, 0.09)
			gf = exp_smooth(gf, 0.2)
		of_path = os.path.join(proc_path, filename)
		ofd = open(of_path, "wr+")
		ofd.write(HEADER)

		'''plot_init()
		plot_addfig(1)
		insert_subplot(411)
		plot_signal(ts, gf, "Smoothed Signal")
		plot_show()'''

		for i in range(0, len(ts)):
			ofd.write(str(ts[i])+","+str(gf[i])+"\n")

def plot_init():
	plt.clf()

def plot_addfig(i):
	plt.figure(i)

def insert_subplot(x):
	plt.subplot(x)

def plot_signal(ts, gf, label):
	plt.plot(ts, gf, label=label)

def plot_show():
	plt.show()

if __name__ == "__main__":
	for i in range(ord('a'), ord('z')+1):
		process_dir(
			os.path.join("../../data/timestamp-vs-gforce/",chr(i)),
			os.path.join("../../data/smoothed-cut-letters/",chr(i))
			)

	process_dir(os.path.join("../../data/timestamp-vs-gforce/","space"),
		    os.path.join("../../data/smoothed-cut-letters/","space"))
	process_dir(os.path.join("../../data/timestamp-vs-gforce/","enter"),
		    os.path.join("../../data/smoothed-cut-letters/","enter"))
