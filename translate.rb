require 'set'
everything = Set.new

def sample(filename, everything, grid,x,y,player,positive)
    s = ""
    n = ""
    (y-4).upto(y+4) do |yy|
        f = grid[yy][x-4..x+4].join()
        n += f
        s += f +"\n"
    end

    if player == 'W'
        s = s.gsub('1','X').gsub('2','1').gsub('X','2')
        n = n.gsub('1','X').gsub('2','1').gsub('X','2')
    end

    n = n[0..39] + n[41..-1]

    inputs = []
    inputs += n.chars.map { |c| c == "0" ? 1 : 0 }
    inputs += n.chars.map { |c| c == "1" ? 1 : 0 }
    inputs += n.chars.map { |c| c == "2" ? 1 : 0 }
    inputs += n.chars.map { |c| c == "3" ? 1 : 0 }

    encoding = "#{inputs.join(',')},#{positive ? 1 : 0}"
    unless everything.include?(encoding)
        File.open(filename, "a+") do |f|
            f << "#{encoding}\n"
        end
        everything << encoding
        return true
    else
        return false
    end
end

# file = "data/2015-05-01-5.sgf"
Dir.glob("data/*.sgf").shuffle.each do |file|
    begin
    csv = file.split(".")[0..-2].join(".")+".csv"
    puts csv
    next if File.exist?(csv)
puts file
sgf = File.read(file)
parts = sgf.split(';')[2..-1]

grid = ([[3]*(4+19+4)] * 4) + ([ ([3]*4)+[0]*19+([3]*4) ] * 19) + ([[3]*(4+19+4)] * 4)
grid = grid.map(&:dup)

parts.each do |move|
    break if move.end_with?('[]')

    #puts move
    player = move[0]
    x = move[2].getbyte(0) - 'a'.getbyte(0) + 4
    y = move[3].getbyte(0) - 'a'.getbyte(0) + 4
    
    sample(csv,everything, grid,x,y,player, true)

    added = false
    remaining = 1000
    while !added && remaining > 0   
        dx = rand(19)+4
        dy = rand(19)+4
        next if dx == x && dy == y
        next unless grid[dy][dx] == 0

        added = sample(csv,everything,grid,dx,dy,player, false)
        remaining -= 1
    end

    # 4.upto(4+19) do |dx|
    #     4.upto(4+19) do |dy|
    #         next unless grid[dy][dx] == 0
    #         next if dx == x && dy == y
    #         sample(everything,grid,dx,dy,player, false)
    #     end
    # end


    #puts "#{player} at #{x},#{y}"
    grid[y][x] = (player == 'B' ? 1 : 2)
    other_color = grid[y][x]

    dirs = [[1,0],[0,1],[-1,0],[0,-1]]
    dirs.each do |check_dir|
        dx = x + check_dir[0]
        dy = y + check_dir[1]
        next unless grid[dy][dx] == other_color
        captured = []
        remaining = [[dx,dy]]
        complete = true
        while remaining.size > 0 && complete
            dx, dy = remaining[0]
            remaining = remaining[1..-1]

            captured << [dx,dy]
            dirs.each do |neighbour_dir|
                nx = dx + neighbour_dir[0]
                ny = dy + neighbour_dir[1]
                if grid[ny][nx] == 0
                    complete = false
                    break
                end 
                n = [nx,ny]
                if grid[ny][nx] == other_color && !captured.include?(n) && !remaining.include?(n)
                    remaining << n
                end
            end
        end

        if complete
            # puts "Capture!"
            # captured.each do |c|
            #     grid[c[1]][c[0]] = ' '
            # end
            # puts (grid.map { |line| line.join() }.join("\n") )
            captured.each do |c|
                grid[c[1]][c[0]] = 0
            end
        end
    end

    print '.'    
end
puts ''
rescue
end
end
