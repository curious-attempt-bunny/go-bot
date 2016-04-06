File.write('data/training.csv', '')
Dir.glob('data/20*.csv').each do |f|
    `sort '#{f}' >> data/training.csv`
end