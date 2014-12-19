package jninethelper.views;



//import TestForm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jninethelper.Activator;
//
//import classlibnoui.ClassNoUI ;
//
//import system.drawing.Size;
//import system.drawing.SizeF;
//import system.windows.forms.Form;
//import system.windows.forms.Application;
import net.sf.jni4net.Bridge;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Combo;








public class JNINETMainPart extends ViewPart {

	public static final String ID = "jniplugin.views.MyFormPart"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Text txtDllPath;
	private Button chkDeleteAllFirst ;
	private Button chkKeepJNINetSystemFile ;
	private Button chkAddSystemFile ;
	
	private String SourceDllPath = "" ;
	private IFile dll ;
	private IFile j4ndll ;
	private IFile j4njar ;
	private IFile j4nSysJarLib ;	
	private IFile j4nUserJarLib ;

	
	private IFile jni4net_jar ;	
	private IFile jni4net_dll ;	
	private IFile jni4net_x64_dll ;	
	
    private File dllStream ;
    private File dllJ4NStream ;
    private File j4njarSteam ;

	private File jni4net_jarStream ;	
	private File jni4net_dllStream ;	
	private File jni4net_x64_dllStream ;	
	private Text txtAdditionalFiles;
 
	Combo cboCurrentBuildConfig ;
	Combo cboTargetBuildConfig ;
	
    
	List<TextReplacement> lst ;

	
	public JNINETMainPart () {
	}

	
	private void getInteropFiles () 
	{
		
		
		
		//IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
//		
//		if ( Activator.getDefault() == null)
//			return ;
		
		
		try
		{
			Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			
		}
		catch (Exception exp1)
		{
			return ;
			
		}
		
		
		try
		
		{
			
			IEditorPart  editorPart =	Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			//Activator.getDefault().getWorkbenchWindow().getActivePage().getActiveEditor();
		
			if(editorPart  == null)
				return ;
		
			SourceDllPath = txtDllPath.getText() ;
			
			File fin = new File ( SourceDllPath ) ;
			
		    String sourceRoot =  FilenameUtils.getFullPathNoEndSeparator(SourceDllPath) ; // "S:\\1\\work\\work" ;
		    String dllName = FilenameUtils.getBaseName(SourceDllPath) ; //.getBaseName(str) + "." +  FilenameUtils.getExtension(str) ;// "ClassLibrary2" ;
		    
		
		    FilenameUtils f = new FilenameUtils () ;
		    
		    IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
		    IFile file =   input.getFile();
		    IProject activeProject = file.getProject();
		    String activeProjectName = activeProject.getName();
			
		
			dll = activeProject.getFile( String.format("%s.dll",  dllName) ) ;
			j4ndll = activeProject.getFile( String.format("%s.j4n.dll",  dllName) ) ;
			j4njar = activeProject.getFile( String.format("%s.j4n.jar",  dllName) ) ;
		
			jni4net_jar = activeProject.getFile( String.format("jni4net.j-0.8.8.0.jar",  dllName) ) ;
			jni4net_dll = activeProject.getFile( String.format("jni4net.n-0.8.8.0.dll",  dllName) ) ;
			jni4net_x64_dll = activeProject.getFile( String.format("jni4net.n.w64.v40-0.8.8.0.dll",  dllName) ) ;

			
		    dllStream =new File( String.format("%s\\%s.dll", sourceRoot, dllName) );
		    dllJ4NStream =new File( String.format("%s\\%s.j4n.dll", sourceRoot, dllName) );
		    j4njarSteam =new File( String.format("%s\\%s.j4n.jar", sourceRoot, dllName) );

		    jni4net_jarStream =new File( String.format("%s\\..\\jni4net.j-0.8.8.0.jar", sourceRoot, dllName) );
		    jni4net_dllStream =new File( String.format("%s\\..\\jni4net.n-0.8.8.0.dll", sourceRoot, dllName) );
		    jni4net_x64_dllStream =new File( String.format("%s\\..\\jni4net.n.w64.v40-0.8.8.0.dll", sourceRoot, dllName) );
		    
		    
            j4nSysJarLib = activeProject.getFile( String.format("Referenced Libraries\\%s.j4n.jar",  dllName) ) ;
            j4nUserJarLib = activeProject.getFile( String.format("Referenced Libraries\\%s.jar",  dllName) ) ;


                            
            
            
		}
		catch (Exception exp)
		{
			exp.printStackTrace(); 
			
		}
          	
        
		
		
	}
	
	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(32, 10, 69, 15);
		toolkit.adapt(lblNewLabel, true, true);
		lblNewLabel.setText("Source DLL");
		
		txtDllPath = new Text(container, SWT.BORDER);
		txtDllPath.setText("S:\\1\\work\\workdll\\ClassLibrary2.dll");
		txtDllPath.setBounds(122, 7, 282, 21);
		toolkit.adapt(txtDllPath, true, true);
		
		Button btnAddDllSupportFiles = new Button(container, SWT.NONE);
		btnAddDllSupportFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
					
				
		        try{
		        	
					
		        	getInteropFiles () ;
		        	
		            dll.delete(true, null );
		            j4ndll.delete(true, null );
		            j4njar.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_jar.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_dll.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_x64_dll.delete(true, null );

		            
		            dll.create(new FileInputStream(dllStream), true, null);
		            j4ndll.create(new FileInputStream(dllJ4NStream), true, null);
		            j4njar.create(new FileInputStream(j4njarSteam), true, null);

		            if ( chkAddSystemFile.getSelection())
		            {
		            	
			            jni4net_jar.create(new FileInputStream(jni4net_jarStream), true, null);
			            jni4net_dll.create(new FileInputStream(jni4net_dllStream), true, null);
			            jni4net_x64_dll.create(new FileInputStream(jni4net_x64_dllStream), true, null);

			            j4nSysJarLib.create(new FileInputStream(jni4net_jarStream), true, null);

		            }
		            
		            
			            
		            j4nUserJarLib.create(new FileInputStream(j4njarSteam), true, null);
		             
		             
		            	    		
					
		        }catch(Exception exp){
		            exp.printStackTrace();
		        }					
				
				
				
				
				
			}
		});
		btnAddDllSupportFiles.setBounds(32, 64, 126, 25);
		toolkit.adapt(btnAddDllSupportFiles, true, true);
		btnAddDllSupportFiles.setText("Add DLL Support files");
		
		Button btnRemoveDllSupportFile = new Button(container, SWT.NONE);
		btnRemoveDllSupportFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				
		        try{
		        	
					
		        	getInteropFiles () ;
		            dll.delete(true, null );
		            j4ndll.delete(true, null );
		            j4njar.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_jar.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_dll.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_x64_dll.delete(true, null );

		            
		             
		    		
					
		        }catch(Exception exp){
		            exp.printStackTrace();
		        }					
				
				
			}
		});
		btnRemoveDllSupportFile.setBounds(32, 95, 141, 25);
		toolkit.adapt(btnRemoveDllSupportFile, true, true);
		btnRemoveDllSupportFile.setText("Remove Dll Support Files");
		
		Button btnUpdateDllFile = new Button(container, SWT.NONE);
		btnUpdateDllFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			
				
		        try{
		        	
					
		        	getInteropFiles () ;
		        	
		            dll.delete(true, null );

		            dll.create(new FileInputStream(dllStream), true, null);
			             
		             
		    		
					
		        }catch(Exception exp){
		            exp.printStackTrace();
		        }					
				
			
			
			}
		});
		btnUpdateDllFile.setBounds(32, 126, 115, 25);
		toolkit.adapt(btnUpdateDllFile, true, true);
		btnUpdateDllFile.setText("Update DLL file");
		
		Button btnBrowseDLL = new Button(container, SWT.NONE);
		btnBrowseDLL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			    dialog
			        .setFilterNames(new String[] { "NET Assembly", "All Files (*.*)" });
			    dialog.setFilterExtensions(new String[] { "*.dll", "*.*" }); 
			    String path = txtDllPath.getText() ;
			    path = FilenameUtils.getFullPathNoEndSeparator(  path ) ;
			    dialog.setFilterPath(path); // Windows path
			    //dialog.setFileName("fred.bat");
			    String str1 =  dialog.open();		
			    if ( str1.length() > 0)
			    	txtDllPath.setText(  str1  );

			    
			}
		});
		btnBrowseDLL.setBounds(410, 10, 20, 15);
		toolkit.adapt(btnBrowseDLL, true, true);
		btnBrowseDLL.setText("...");
		
		Button btnTest = new Button(container, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try
				
				{
					
				 	Bridge.init();

			        Bridge.LoadAndRegisterAssemblyFrom(new java.io.File("S:\\JavaDev\\jninethelper\\jni4net.n-0.8.8.0.dll"));
			    
			        
			        
			    	Bridge.init();
			    	
			    	
			        //Bridge.LoadAndRegisterAssemblyFrom(new java.io.File("S:\\JavaDev\\jninethelper\\ClassLibNoUI.j4n.dll"));
			        
			    	
			    	
			        Bridge.LoadAndRegisterAssemblyFrom(new java.io.File("S:\\JavaDev\\jninethelper\\ClassLibrary2.j4n.dll"));
			        //Form1 f = new Form1 ( ) ;
			        classlibrary2.Class1 f = new classlibrary2.Class1  () ;
			        f.SayHello() ;
			        //f.ShowWin(); 

//
//			        Bridge.LoadAndRegisterAssemblyFrom(new java.io.File("S:\\JavaDev\\jninethelper\\JavaWrapper.j4n.dll"));
//			        //Form1 f = new Form1 ( ) ;
//			        javawrapper.Class1 f = new javawrapper.Class1  () ;
//			        
//					//MessageDialog.openInformation( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),	"Hello",	f.getClassName() );
//			        f.ShowTemplateManager();
//			        //f.getClassName() ; 
			        
			        
			        
			    	
//					Bridge.LoadAndRegisterAssemblyFrom(new File("S:\\JavaDev\\jninethelper\\winforms.dll"));
//
//					TestForm test=new TestForm();
//					test.init();
//					test.ShowDialog() ;			
					
					
				}
				catch (Exception exp)
				{
					MessageDialog.openInformation(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Error",
							exp.getMessage() );
					
				}
			
			
			}
		});
				
				
				
		btnTest.setBounds(485, 10, 75, 25);
		toolkit.adapt(btnTest, true, true);
		btnTest.setText("Test");
		
		chkDeleteAllFirst = new Button(container, SWT.CHECK);
		chkDeleteAllFirst.setBounds(182, 67, 75, 16);
		toolkit.adapt(chkDeleteAllFirst, true, true);
		chkDeleteAllFirst.setText("Delete first");
		
		chkAddSystemFile = new Button(container, SWT.CHECK);
		chkAddSystemFile.setBounds(290, 67, 93, 16);
		toolkit.adapt(chkAddSystemFile, true, true);
		chkAddSystemFile.setText("Add system files");
		
		chkKeepJNINetSystemFile = new Button(container, SWT.CHECK);
		chkKeepJNINetSystemFile.setBounds(196, 95, 109, 16);
		toolkit.adapt(chkKeepJNINetSystemFile, true, true);
		chkKeepJNINetSystemFile.setText("Keep System File");
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setBounds(32, 31, 94, 15);
		toolkit.adapt(lblNewLabel_1, true, true);
		lblNewLabel_1.setText("Additional files");
		
		txtAdditionalFiles = new Text(container, SWT.BORDER);
		txtAdditionalFiles.setBounds(132, 31, 298, 21);
		toolkit.adapt(txtAdditionalFiles, true, true);
		
		Button btnTest1 = new Button(container, SWT.NONE);
		btnTest1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
//				IFile[] files2 = ResourcesPlugin.getWorkspace().getRoot().getFilters().findFilesForLocationURI(new URI("file:/"+workingDirectory));
//				for (IFile file : files2) {
//				   System.out.println("fullpath " +file.getFullPath());
//				}
//				

				
				try {
					
					processContainer (getActiveProject (), lst , cboCurrentBuildConfig.getText()  , cboTargetBuildConfig.getText()  ) ;
					
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
			}
		});
		btnTest1.setBounds(580, 5, 75, 25);
		toolkit.adapt(btnTest1, true, true);
		btnTest1.setText("Test1");
		
		cboCurrentBuildConfig = new Combo(container, SWT.NONE);
		cboCurrentBuildConfig.setBounds(400, 110, 182, 23);
		toolkit.adapt(cboCurrentBuildConfig);
		toolkit.paintBordersFor(cboCurrentBuildConfig);
		
		cboTargetBuildConfig = new Combo(container, SWT.NONE);
		cboTargetBuildConfig.setBounds(640, 110, 158, 23);
		toolkit.adapt(cboTargetBuildConfig);
		toolkit.paintBordersFor(cboTargetBuildConfig);
		
		
		//cboCurrentBuildConfig.add(string);
		
		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setBounds(410, 69, 55, 15);
		toolkit.adapt(lblNewLabel_2, true, true);
		lblNewLabel_2.setText("Current ");
		
		Label lblNewLabel_3 = new Label(container, SWT.NONE);
		lblNewLabel_3.setBounds(640, 67, 55, 15);
		toolkit.adapt(lblNewLabel_3, true, true);
		lblNewLabel_3.setText("Target");
		
		Button btnParseConfig = new Button(container, SWT.NONE);
		btnParseConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					DOMParserDemo dom = new DOMParserDemo () ;
					lst =  dom.parseBuildParam(  getActiveProject ().getFile("BuildParam.xml").getRawLocation ().toOSString()   );
					for ( TextReplacement tp : lst)
					{
						cboCurrentBuildConfig.add (tp.BuildName) ;
						cboTargetBuildConfig.add (tp.BuildName) ;
						
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			
			}
		});
		btnParseConfig.setBounds(688, 5, 93, 25);
		toolkit.adapt(btnParseConfig, true, true);
		btnParseConfig.setText("Parse Config");

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	
	
	IProject getActiveProject ()
	{
		
			
		IEditorPart  editorPart =
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				//Activator.getDefault().getWorkbenchWindow().getActivePage().getActiveEditor();

		if(editorPart  != null)
		{
		    IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
		    IFile file = input.getFile();
		    IProject activeProject = file.getProject();
		    return activeProject ;
		    
		}
		else
			
			return null ;
		
		
	}
	
	void processContainer(IContainer container, List<TextReplacement> lst, String fromBuild, String toBuild ) throws CoreException
	{
	   IResource [] members = container.members();
	   for (IResource member : members)
	    {
	       if (member instanceof IContainer)
	         processContainer((IContainer)member, lst, fromBuild, toBuild );
	       else if (member instanceof IFile)
	       {
	    	   IFile f = (IFile)member ;
	    	   String fullPath = f.getRawLocation ().toOSString() ;
	    	   System.out.println( fullPath  );

	    	   String fromText = null ;
	    	   String toText = null ;
	    	   
	    	   for ( TextReplacement  tp : lst  )
	    	   {
	    		   
		    	   if ( tp.FileName.endsWith(f.getName() ) && tp.BuildName.equals(toBuild ) )
		    	   {
		    		   fromText = getFromText ( lst, fromBuild, tp.ReplacementID) ;
		    		   toText = tp.ToText ;
		    		   replaceText ( fullPath, fromText, toText ) ;
		    		   
		    	   }
	    		   
	    	   }
	    	   
	    	   
	       }
	    }
	} 
	
	
	
	private String getFromText (  List<TextReplacement> lst, String buildName, String ReplacementID )
	{
		
 	   for ( TextReplacement  tp : lst  )
	   {
 		   if ( tp.BuildName.equals( buildName) && tp.ReplacementID.equals(ReplacementID)  )
 			   return tp.ToText  ;
 		   
	   }
 	   
 	   return null ;
 	   
		
		
	}
	
	public void replaceText (String fileIn, String fromText, String toText )
	{
		
		Path path = Paths.get(fileIn);
		Charset charset = StandardCharsets.UTF_8;

		String content;
		try {
			
			content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll(fromText, toText);
			Files.write(path, content.getBytes(charset));	

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
