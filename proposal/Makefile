all:	report.pdf clean

report.pdf: report.tex report.bib
	pdflatex report
	-bibtex report
	pdflatex report
	pdflatex report

quick:
	pdflatex report

clean:
	rm -f *.aux *.bbl *.blg *.log *.dvi *.bak *~ *.out
