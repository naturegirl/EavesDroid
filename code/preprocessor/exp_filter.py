import math
import sys
import os
# import matplotlib.pyplot as plt

def load_csv(filename):
	ts = []
	xs = []
	ys = []
	zs = []
	df = open(filename, "r")
	header = df.readline()
	for line in df:
		sa = line.split(",")
		ts.append(int(sa[0]))
		xs.append(float(sa[1]))
		ys.append(float(sa[2]))
		zs.append(float(sa[3]))
	return ts, xs, ys, zs

def exp_smooth(data, alpha):
	output = [data[0]]
	for i in range(1,len(data)):
		output.append(output[i-1] + alpha * (data[i] - output[i-1]))
	return output

def snip_ends(ts_diff, ts, gf, xs, ys, zs):
	start = ts_diff[0]
	for i in range(1, len(ts_diff)):
		if ts_diff[i] >= start + 150000:
			break;
	si = i
	end = ts_diff[len(ts_diff)-1]
	for i in range(1, len(ts_diff)):
		if ts_diff[i] >= end - 150000:
			break;
	ei = i
	return ts_diff[si:ei],ts[si:ei],gf[si:ei],xs[si:ei],ys[si:ei],zs[si:ei]

def compute_gforce(xs, ys, zs):
	zipped = zip(xs, ys, zs)
	gf = map(lambda (x, y, z): math.sqrt(x*x+y*y+z*z)-9.81, zipped)
	return gf

def process_dir(path, outdir, smooth=True):
	proc_path = outdir
	if not os.path.exists(proc_path):
		try:
			bashCmd = "mkdir -p " + proc_path
			os.system(bashCmd)
		except OSError as error:
			print error
	for filename in os.listdir(path):
		file_path = os.path.join(path, filename)
		if os.path.isdir(file_path):
			continue;
		ts,xs,ys,zs = load_csv(file_path)
		ts_diff = map(lambda x: (x - ts[0])/1000, ts)
		gf = compute_gforce(xs, ys, zs)
		ts_diff,ts,gf,xs,ys,zs = snip_ends(ts_diff, ts, gf, xs, ys, zs)
		if smooth:
			gf = exp_smooth(gf, 0.09)
			gf = exp_smooth(gf, 0.2)
		of_path = os.path.join(proc_path, filename)
		ofd = open(of_path, "wr+")

		'''plot_init()
		plot_addfig(1)
		insert_subplot(411)
		plot_signal(ts, gf, "Smoothed Signal")
		plot_show()'''

		for i in range(0, len(ts)):
			ofd.write(str(ts[i]) + "," + str(xs[i]) + "," +
				  str(ys[i]) + "," + str(zs[i]) + "\n")

#def plot_init():
#	plt.clf()

#def plot_addfig(i):
#	plt.figure(i)

#def insert_subplot(x):
#	plt.subplot(x)

#def plot_signal(ts, gf, label):
#	plt.plot(ts, gf, label=label)

#def plot_show():
#	plt.show()

if __name__ == "__main__":
	if len(sys.argv) != 3:
		print 'Usage: python exp_filter <input-dir> <output-dir>'
		sys.exit(1)
	for d in os.listdir(sys.argv[1]):
		process_dir(
			os.path.join(sys.argv[1], d),
			os.path.join(sys.argv[2], d)
			)
	print 'written to ', sys.argv[2]
