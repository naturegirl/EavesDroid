all:	template.pdf clean

template.pdf: template.tex sigproc.bib
	pdflatex template
	-bibtex template
	pdflatex template
	pdflatex template

quick:
	pdflatex template

clean:
	rm -f *.aux *.bbl *.blg *.log *.dvi *.bak *~ *.out
