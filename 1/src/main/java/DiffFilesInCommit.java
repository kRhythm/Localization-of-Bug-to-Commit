import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;



public class DiffFilesInCommit {

    public static void main(String[] args) throws Exception {
    	String REMOTE_URL = "https://github.com/kRhythm/Localization-of-Bug-to-Commit.git";
    	Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(new File("C:\\Users\\nvuggam\\Desktop\\DCA"))
                .call();

    	try (Repository repository = result.getRepository()) {
            
            Collection<Ref> allRefs = repository.getAllRefs().values();

            try (RevWalk revWalk = new RevWalk( repository )) {
                for( Ref ref : allRefs ) {
                    revWalk.markStart( revWalk.parseCommit( ref.getObjectId() ));
                }
                System.out.println("Walking all commits starting with " + allRefs.size() + " refs: " + allRefs);
                int count = 0;
                RevCommit previouscommit = null;
                RevCommit presentcommit = null;
                for( RevCommit commit : revWalk ) 
                {
                	if(count==0) {
                		previouscommit = commit;
                	}
                	else
                	{
                		presentcommit = commit;
                		try (Git git = new Git(repository)) 
                		{
                			RevTree previoustree = previouscommit.getTree();
                			RevTree presenttree = presentcommit.getTree();
                			File FileInfo= new File("C:\\Users\\nvuggam\\Desktop\\FileInfo.txt");
                			PrintStream consolee = System.out; 
                	    	PrintStream hi = new PrintStream(FileInfo);
                	    	System.setOut(hi);
                			listDiff(repository, git,presentcommit,previouscommit);
                			BufferedReader Fi = new BufferedReader(new FileReader(FileInfo));
                			String Fline = null;
                			ArrayList<String> OriginalM = new ArrayList<String>();
                			ArrayList<String> RevisedM = new ArrayList<String>();
                			ArrayList<String> Added = new ArrayList<String>();
                			ArrayList<String>  Deleted = new ArrayList<String>();
                			while(  (Fline = Fi.readLine()) != null )
                			{
                				String[] WordsOfLine = Fline.split(" ",3) ;
                				System.setOut(consolee);
                				System.out.println(WordsOfLine[0] + " " + WordsOfLine[1] + " " + WordsOfLine[2]);
                				System.setOut(hi);
                				if(WordsOfLine[0].equals("MODIFY"))
                				{
                					//System.out.println("hi");
                					OriginalM.add(WordsOfLine[1]);
                					RevisedM.add(WordsOfLine[2]);      
                				}
                			}Fi.close();
                			//System.out.println(OriginalM.size());
                			for(int i=0;i<OriginalM.size();i++)
                			{
                				File OringinalFile = new File("C:\\Users\\nvuggam\\Desktop\\Original.java");
                				File RevisedFile = new File("C:\\Users\\nvuggam\\Desktop\\Revised.java");
                				PrintStream OF = new PrintStream(OringinalFile);
                				PrintStream RF = new PrintStream(RevisedFile);
                				String O = OriginalM.get(i);
                				try (TreeWalk treeWalk = new TreeWalk(repository)) {
                                    treeWalk.addTree(presenttree);
                                    treeWalk.setRecursive(true);
                                    treeWalk.setFilter(PathFilter.create(O));
                                    ObjectId objectId = treeWalk.getObjectId(0);
                                    ObjectLoader loader = repository.open(objectId);
                                    System.setOut(OF);
                                    loader.copyTo(System.out);
                				}
                				String R = RevisedM.get(i);
                				try (TreeWalk treeWalk = new TreeWalk(repository)) {
                                    treeWalk.addTree(previoustree);
                                    treeWalk.setRecursive(true);
                                    treeWalk.setFilter(PathFilter.create(R));
                                    ObjectId objectId = treeWalk.getObjectId(0);
                                    ObjectLoader loader = repository.open(objectId);
                                    System.setOut(RF);
                                    loader.copyTo(System.out);
                				}
                				
                				graph_diff.main(null);  
                				PrintStream console = System.out;
                                System.setOut(console);
                                System.out.println("Had " + count + " commits");
                			}
                		}
                	}
                    count++;
                    
                }
                
            }
        }
    }
    
    private static void listDiff(Repository repository, Git git, ObjectId oldCommit, ObjectId newCommit) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(repository, oldCommit))
                .setNewTree(prepareTreeParser(repository, newCommit))
                .call();

        System.out.println("Found: " + diffs.size() + " differences");
        for (DiffEntry diff : diffs) {
            System.out.println(diff.getChangeType() + " " + diff.getOldPath() + " " + diff.getNewPath() );
        }
    }
    
    private static AbstractTreeIterator prepareTreeParser(Repository repository, ObjectId objectId) throws IOException {

        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(objectId);
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }
    
    
}    
    
        
  