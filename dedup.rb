require 'set'
positive = Dir.glob("data/positive/*.txt").map { |f| f = f.split('/')[-1] }
negative = Dir.glob("data/negative/*.txt").map { |f| f = f.split('/')[-1] }
dups = positive & negative
puts positive.size
puts negative.size
puts dups.size
# dups.each do |dup|
#     f = "data/negative/#{dup}"
#     # puts f
#     `rm #{f}`
# end
