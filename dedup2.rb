require 'set'
# sort data/training.csv | uniq > data/training.sorted.csv
File.open("data/training.consistent.csv", "w") do |f|
    last = ''
    File.new('data/training.sorted.csv').each_line do |line|
        f << "#{line}" unless line.split(",")[0..-2] == last.split(",")[0..-2]
        last = line
    end
end
