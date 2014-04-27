NUM_FEATURES = 15   # frequency range is divided into 15 parts
inputdir <- "./../../data/timestamp-vs-gforce"
diagram_outputdir <- "./../../data/spectograms/"
feature_outputdir <- "./../../data/features/"
files <- list.files(inputdir, full.names = TRUE)
files

num_dirs <- length(files)

for (j in 1:num_dirs) {
  letter_files <- list.files(files[j], full.names = TRUE)
  letter_files
  current_letter <- basename(files[j])
  
  # create output matrix that we will write to csv for each letter
  output <- matrix(, 0, NUM_FEATURES) 
  labels <- paste('fft', 1:NUM_FEATURES, sep="");
  colnames(output) <- labels
  
  num_letter_files <- length(letter_files)
  for (k in 1:num_letter_files) {
    
    # read data
    data <- read.delim(letter_files[k], header = FALSE, sep = ",", quote = "",
                   dec = ".", fill = TRUE, comment.char = "")
        
    # construct the filename for the jpeg
    pos <- regexpr(".gforce.csv", letter_files[k], fixed=T)[1]
    filename <- substr(letter_files[k], 0, pos-1)
    filename <- basename(filename)
    
    # uncomment if you want to write the spectogram
    #write.spectogram(data, current_letter, filename);
    
    # compute and append feature
    y <- as.vector(data$V2);
    y <- as.numeric(y[2:length(y)])
    fft <- spec.ar(y, n.freq=NUM_FEATURES, plot=F);
    output <- rbind(output, t(fft$spec))  # append row
    #print(output)
  }
  # write csv file
  
  csv_filename <- paste(feature_outputdir, 'fft_', current_letter, '.csv', sep="");
  write.csv(output, csv_filename, row.names=F)
  print(paste('writing', csv_filename))
}

# @data: data read from csv file
# current_letter: 'a', 'b', ...
# filename: filename stripped of file extension, i.e. 'a_1397933294'
write.spectogram <- function(data, current_letter, filename) {
  dir.create(paste(diagram_outputdir, current_letter,sep=""), showWarnings = F)
  filename <- paste(diagram_outputdir, current_letter, '/',filename,".jpg", sep="")
  
  # data
  y <- as.vector(data$V2);
  y <- as.numeric(y[2:length(y)])
  
  # write the plot to the jpeg
  jpeg(filename)
  spec.ar(y);
  dev.off() 
  print(paste('writing', filename))

}

