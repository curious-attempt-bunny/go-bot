Dir.glob("data/*.sgf").each do |f|
   `rm #{f}` if File.read(f).include?("HA[")
end