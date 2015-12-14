package evaluation;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public abstract class QueryParser {
	protected transient ArrayList<File> files;
	protected transient int curF=0;
	protected transient RandomAccessFile br;
	protected transient RandomAccessFile rel;
	protected String begin;
	protected String end;
	
	public QueryParser(String begin, String end){
		this.begin=begin;
		this.end=end;
	}
	
	public QueryParser(String begin){
		this(begin,"");
	}
	
	public void init(String filename, String fileRel){
		try {
			files=getAllFiles(new File(filename));
			curF=0;
			br=new RandomAccessFile(files.get(curF),"r");
			rel=new RandomAccessFile(fileRel,"r");
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public Query getQuery(int numQuery) throws IOException{
		br.seek(0);
		Query q = null;
		int cpt = 1;
		while((q=nextQuery())!=null){
			if(cpt==numQuery) return q;
			cpt++;
		}
		return q;
	}
	
	public Query nextQuery() throws IOException {
		Query query = null;
		String ligne = "";
		boolean ok=false;
		while(!ok){
			StringBuilder st=new StringBuilder();
			boolean read=false;
			long start=0;
			long nbBytes=0;
			while(true){
				long curOff=br.getFilePointer();
				ligne=br.readLine();
				if(ligne==null){
					if((this.end.length()==0) && read){
						nbBytes=curOff-start;
						read=false;
						ok=true;
					}
					break;
				}
				
				if(ligne.startsWith(this.begin)){
					if((this.end.length()==0) && read){
						nbBytes=curOff-start;
						read=false;
						ok=true;
						br.seek(curOff);
						break;
					}
					else{
						read=true;
						start=curOff;
					}
				}
				if(read){
					st.append(ligne+"\n");
				}
				if((this.end.length()>0) && (ligne.startsWith(this.end))){
					read=false;
					ok=true;
					try {
						nbBytes=br.getFilePointer()-start;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
			if (ok){
				String s=st.toString();
				//String from=files.get(curF).getAbsolutePath()+";"+start+";"+nbBytes;
				rel.seek((long) 0);
				query=getQuery(s);
			}
			else{
				br.close();
				curF++;
				if(curF<files.size()){
					br=new RandomAccessFile(files.get(curF),"r");
				}
				else{
					return null;
				}
			}
		}
		return query;
	}
	
	public abstract Query getQuery(String str);
	
	public ArrayList<File> getAllFiles(File file){
		ArrayList<File> ret=new ArrayList<File>();
		if(file.isDirectory()){
			File[] files=file.listFiles();
			for(File f:files){
				ret.addAll(getAllFiles(f));
			}
		}
		else{
			ret.add(file);
		}
		return ret;
	}

	public RandomAccessFile getBr() {
		return br;
	}

	public RandomAccessFile getRel() {
		return rel;
	}
	
}

	