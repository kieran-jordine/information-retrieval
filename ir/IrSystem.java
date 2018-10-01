package ir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IrSystem {
	
	//<title, [tokens]>
	private final Map<String, List<String>> corpus = new HashMap<>();
	private final Set<String> vocabulary = new HashSet<>();
	//<token, <title, numApperances>>
	private final Map<String, Map<String, Integer>> tfidf = new HashMap<>();
	//<title, magnitude>
	private final Map<String, Double> documentMagnitude = new HashMap<>();
	
	public IrSystem() { }
	
	public IrSystem(String dataDirectory) {
		readData(new File(dataDirectory));
	}
	
	public IrSystem(File file) {
		readData(file);
	}
	
	public List<String> queryRank(String query) {
		//List<String> tokens = tokenize(query);
		return null;
	}
	
	public List<String> tokenize(String query) {
		final PorterStemmer stemmer = new PorterStemmer();
		return Arrays.asList(query.trim().toLowerCase().split("\\W+")).stream()
				.map(token -> stemmer.stem(token))
				.collect(Collectors.toList());
	}
	
	public void cosineSimilarity(String query) {
		Map<String, Double> results = new LinkedHashMap<>();
		List<String> tokens = tokenize(query);
		Map<String, Integer> qm = new HashMap<>(); //tokens.stream().collect(Collectors.tom)
		for (String token : tokens) {
			qm.put(token, qm.containsKey(token) ? qm.get(token) + 1 : 1);
		}
		//System.out.println(qm);
		Map<String, Double> qv = new HashMap<>();
		qm.entrySet().forEach(k -> {
			qv.put(k.getKey(), 1 + Math.log10(k.getValue()));
		});
		System.out.println("qv: "+qv);
		corpus.entrySet().stream().forEach(doc -> {
			String title = doc.getKey();
			//if (title.equalsIgnoreCase("A Winter Pilgrimage (1901) 0600121.txt")) {
				Map<String, Double> dv = new HashMap<>();
				double magnitude = documentMagnitude.get(title);
				//System.out.println(magnitude);
				qv.entrySet().stream().forEach(token -> {
					//System.out.println(token.getKey()+" = "+getTfIdf(token.getKey(), title));
					//System.out.println(", "+corpus.get(title).contains(token.getKey()));
					dv.put(token.getKey(), getTfIdf(token.getKey(), title) / magnitude);
				});
				//System.out.println("dv: "+dv);
				results.put(title, dotProduct(qv, dv));
				//System.exit(1);
			//}
		});
		results.entrySet().stream()
			.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
			.limit(10)
			.forEach(System.out::println);
	}
	
	private double dotProduct(Map<String, Double> qv, Map<String, Double> dv) {
		double dp = 0;
		for (Map.Entry<String, Double> t : qv.entrySet()) {
			dp += t.getValue() * dv.get(t.getKey()).doubleValue();
		}
		return dp;
	}
	
	/**
	 * Return the tf-idf weight for the given word in the document.<br><br>
	 * tf = # of times word occur in doc<br>
	 * tf_weight = 1 + log10(tf) [or 0 if tf==0]<br><br>
	 * df = # of different documents word appears in<br>
	 * N = # of documents<br>
	 * idf_weight = log10(N / df) [or 0 if df==0]<br><br>
	 * 
	 * @return tf_weight * idf_weight
	 * 
	 */
	private double getTfIdf(String token, String title) {
		double tf = tfidf.getOrDefault(token, new HashMap<>()).getOrDefault(title, 0);
		double tf_weight = (tf == 0) ? 0 : 1 + Math.log10(tf);		
		double df = tfidf.getOrDefault(token, new HashMap<>()).size();
		double idf_weight = (df == 0) ? 0 : Math.log10(corpus.size() / df);
		return tf_weight * idf_weight;
	}
	
	public Map<String, Integer> getPostings(String term) {
		PorterStemmer stemmer = new PorterStemmer();
		return tfidf.get(stemmer.stem(term));
	}
		
	private double getTfIdf2(String token, String title) {
		double tf = tfidf.getOrDefault(token, new HashMap<>()).getOrDefault(title, 0);
		double tf_weight = (tf == 0) ? 0 : 1 + Math.log10(tf);		
		double df = tfidf.getOrDefault(token, new HashMap<>()).size();
		double idf_weight = (df == 0) ? 0 : Math.log10(corpus.size() / df);
		System.out.println(token +" ~ "+ title);
		System.out.println("df: "+df+", tf: "+tf);
		return tf_weight * idf_weight;
	}
	
	private void readData(File file) {
		try {
			//Files.delete(Paths.get(new File(file, "stemmed").getAbsolutePath()));
			System.out.println("reading data");
			if (new File(file, "stemmed").exists()) {
			//	System.out.println("stemmed already");
				readStemmedData(file);
			} else {
				stemData(file);				
			}			
		} catch(Exception ex) {
			System.err.println(ex.getLocalizedMessage());
		}
	}
	
	private void stemData(File file) {
		try {
			System.out.println("stemming data");
			final File rawDir = new File(file, "raw");
			final File stemmedDir = new File(file, "stemmed");
			if (!stemmedDir.exists()) {
				Files.createDirectory(stemmedDir.toPath());
			}
			if (rawDir.exists() && rawDir.isDirectory()) {
				System.out.println("raw dir exists\n");
				/*Files.list(Paths.get(rawDir.getAbsolutePath())) //Stream<Path>
					.filter(Files::isRegularFile)
					.limit(2)
					.forEach(System.out::println);*/
				final PorterStemmer stemmer = new PorterStemmer();
				Files.newDirectoryStream(Paths.get(rawDir.getAbsolutePath()), path -> path.toFile().isFile()) //DirectoryStream<Path>
					.forEach(path -> {
						System.out.println("***filename: " + path.getFileName());
						try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(stemmedDir.getAbsolutePath(), path.getFileName().toString()))) {
							try (Stream<String> stream = Files.lines(path)) {
								stream
									.map(line -> line.trim())
									.filter(line -> !line.isEmpty())
									.map(line -> Arrays.asList(line.split("\\W+")))
									.forEach(tokens -> {
										try {
											//System.out.println(tokens);
											String stemmedLine = tokens.stream()
												.map(token -> token.toLowerCase())
												.map(token -> stemmer.stem(token))
												.collect(Collectors.joining(" "));
											//System.out.println(stemmedLine);
											writer.write(stemmedLine);
											writer.newLine();
										} catch (IOException e) {
											System.err.println(e.getLocalizedMessage());
										}
									});
								writer.flush();
								writer.close();
							} catch (IOException e) {
								System.err.println(e.getLocalizedMessage());
							}
						} catch (IOException we) {
							System.err.println(we.getLocalizedMessage());
						}
					});
			}
		} catch(Exception ex) {
			System.err.println(ex.getLocalizedMessage());
		}
	}

	private void readStemmedData(File file) {
		File stemmed = new File(file, "stemmed");
		if (stemmed.exists() && stemmed.isDirectory()) {
			try (DirectoryStream<Path> ds = Files.newDirectoryStream(stemmed.toPath(), path -> path.toFile().isFile())) {
				System.out.println("building corpus ...");
				ds.forEach(path -> {
					try (Stream<String> read = Files.lines(path)) {
						final List<String> tokens = new ArrayList<>();
						read
							.map(line -> Arrays.asList(line.split("\\W+")))
							.forEach(array -> {
								array.forEach(token -> {
									tokens.add(token);
								});
							});
						corpus.put(path.getFileName().toString(), tokens);
						vocabulary.addAll(tokens);
					} catch (IOException e) {
						System.err.println(e.getLocalizedMessage());
					}					
				});
			} catch (IOException ee) {
				System.err.println(ee.getLocalizedMessage());
			}
			try (DirectoryStream<Path> ds = Files.newDirectoryStream(stemmed.toPath(), path -> path.toFile().isFile())) {
				System.out.println("building tfidf and magnitude ...");
				ds.forEach(path -> {
					String fileName = path.getFileName().toString();
					//System.out.println(fileName);
					try (Stream<String> read = Files.lines(path)) { 
						List<String> tokens = new ArrayList<>();
						read
							.map(line -> Arrays.asList(line.split("\\W+")))
							.forEach(array -> {
								array.forEach(token -> {
									tokens.add(token);
								});
							});
						//System.out.println("   computing tfidf ...");
						tokens.forEach(token -> {
							if (!tfidf.containsKey(token)) {
								tfidf.put(token, new HashMap<>());
							}
							if (!tfidf.get(token).containsKey(fileName)) {
								tfidf.get(token).put(fileName, 0);
							}
							int num = tfidf.get(token).get(fileName) + 1;
							tfidf.get(token).put(path.getFileName().toString(), num);
						});
						//System.out.println("   computing document magnitude ...");
						Set<String> hs = new HashSet<>(tokens);
						double n = 0;
						for (String unique : hs) {
							n = n + Math.pow(getTfIdf(unique, fileName), 2);
						}
						documentMagnitude.put(fileName, Math.sqrt(n));
					} catch (IOException e) {
						System.err.println(e.getLocalizedMessage());
					}					
				});
				//System.out.println(corpus.size()+" "+documentMagnitude.size());
			} catch (IOException ee) {
				System.err.println(ee.getLocalizedMessage());
			}
		}
	}

}