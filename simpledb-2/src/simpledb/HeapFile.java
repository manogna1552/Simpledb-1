package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples in no particular order. Tuples are
 * stored on pages, each of which is a fixed size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 */
public class HeapFile implements DbFile {

	/**
	 * The File associated with this HeapFile.
	 */
	protected File file;

	/**
	 * The TupleDesc associated with this HeapFile.
	 */
	protected TupleDesc td;

	/**
	 * Constructs a heap file backed by the specified file.
	 * 
	 * @param f
	 *            the file that stores the on-disk backing store for this heap file.
	 */
	public HeapFile(File f, TupleDesc td) {
		this.file = f;
		this.td = td;
	}

	/**
	 * Returns the File backing this HeapFile on disk.
	 * 
	 * @return the File backing this HeapFile on disk.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns an ID uniquely identifying this HeapFile. Implementation note: you will need to generate this tableid
	 * somewhere ensure that each HeapFile has a "unique id," and that you always return the same value for a particular
	 * HeapFile. We suggest hashing the absolute file name of the file underlying the heapfile, i.e.
	 * f.getAbsoluteFile().hashCode().
	 * 
	 * @return an ID uniquely identifying this HeapFile.
	 */
	public int getId() {
		return file.getAbsoluteFile().hashCode();
	}

	/**
	 * Returns the TupleDesc of the table stored in this DbFile.
	 * 
	 * @return TupleDesc of this DbFile.
	 */
	public TupleDesc getTupleDesc() {
		return td;
	}

	// see DbFile.java for javadocs

	public Page readPage(PageId pid){
		// some code goes here
		//throw new UnsupportedOperationException("Implement this");
				try {
					if (pid.getTableId() != getId()) {
						throw new IllegalArgumentException("page not found in file");
					} 
					RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
					byte[] data1 = new byte[BufferPool.PAGE_SIZE];	
					randomAccessFile.seek(pid.pageno() * BufferPool.PAGE_SIZE);		
					randomAccessFile.readFully(data1);
					randomAccessFile.close();
					HeapPage heapPage = new HeapPage((HeapPageId)pid, data1); 
					return (Page)heapPage ;			
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}catch (IOException e) {
						e.printStackTrace();
					}
				return null;
			
	}

	// see DbFile.java for javadocs
	public void writePage(Page page) throws IOException {
		// some code goes here
		// not necessary for assignment1
	}

	/**
	 * Returns the number of pages in this HeapFile.
	 */
	public int numPages() {
		return (int) (file.length() / BufferPool.PAGE_SIZE);
	}

	// see DbFile.java for javadocs
	public ArrayList<Page> addTuple(TransactionId tid, Tuple t)
			throws DbException, IOException, TransactionAbortedException {
		// some code goes here
		// not necessary for assignment1
		return null;
	}

	// see DbFile.java for javadocs
	public Page deleteTuple(TransactionId tid, Tuple t) throws DbException, TransactionAbortedException {
		// some code goes here
		// not necessary for assignment1
		//return null;
		 PageId pid = t.getRecordId().getPageId();
		 if((t.getRecordId() == null) || pid.pageno() >= numPages())
			 throw new DbException("tuple is not found or page number of tuple is wrong");
		 else {
			 HeapPage hpage = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
			 hpage.deleteTuple(t);
			 return hpage;
		 }
	}

	// see DbFile.java for javadocs
	public DbFileIterator iterator(TransactionId tid) {

		return new DbFileIterator() {

			/**
			 * The ID of the page to read next.
			 */
			int nextPageID = 0;

			/**
			 * An iterator over all tuples from the page read most recently.
			 */
			Iterator<Tuple> iterator = null;

			@Override
			public void open() throws DbException, TransactionAbortedException {
				nextPageID = 0;		
				// obtains an iterator over all tuples from page 0
				/*Iterator<Tuple> iterator1 = ((HeapPage) Database.getBufferPool().getPage(tid, new HeapPageId(getId(),nextPageID++),
						Permissions.READ_WRITE)).iterator();*/
				ArrayList<Tuple> totalTupleList = new ArrayList<Tuple>();
				for (int i = 0; i < numPages(); i++) {				 
				Iterator<Tuple> iterator1 = ((HeapPage) Database.getBufferPool().getPage(tid, new HeapPageId(getId(), nextPageID++),
						Permissions.READ_WRITE)).iterator();
				if(iterator1 != null)
				while(iterator1.hasNext()) {
					totalTupleList.add(iterator1.next());
				}
				}
				iterator = totalTupleList.iterator();
				/*System.out.println("iterator"+iterator);
				System.out.println("np"+nextPageID);*/
			}

			@Override
			public boolean hasNext() throws DbException, TransactionAbortedException {
				// some code goes here
				//throw new UnsupportedOperationException("Implement this");
				if(iterator == null) {
					return false; 
				}
				else if(iterator.hasNext()) {
					return true;
				} else
					return false;
					
			}

			@Override
			public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
				// some code goes here
				//throw new UnsupportedOperationException("Implement this");
				if(iterator != null) {	
					if(iterator.hasNext()) {
						Tuple tuple1 = iterator.next();
						return tuple1;
				    } else {
				    	throw new NoSuchElementException("no such element found");				
				      }
				} else {
					throw new NoSuchElementException("no such Element found");
				}
			}

			@Override
			public void rewind() throws DbException, TransactionAbortedException {
				close();
				open();
			}

			@Override
			public void close() {
				iterator = null;
			}

		};
	}

}
